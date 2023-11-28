package codes.laivy.data.mysql.variable;

import codes.laivy.data.Main;
import codes.laivy.data.data.Data;
import codes.laivy.data.mysql.data.MysqlData;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.utils.SqlUtils;
import codes.laivy.data.mysql.variable.type.Type;
import codes.laivy.data.variable.Variable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MysqlVariable<T> extends Variable<T> {

    private final @NotNull MysqlTable table;
    private final @NotNull Type<T> type;

    private final @UnknownNullability T defaultValue;
    private final boolean nullable;

    public MysqlVariable(@NotNull String id, @NotNull MysqlTable table, @NotNull Type<T> type, @UnknownNullability T defaultValue) {
        this(id, table, type, defaultValue, true);
    }
    public MysqlVariable(@NotNull String id, @NotNull MysqlTable table, @NotNull Type<T> type, @UnknownNullability T defaultValue, boolean nullable) {
        super(id);

        this.table = table;
        this.type = type;
        this.defaultValue = defaultValue;

        this.nullable = nullable;

        if (!id.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This variable id '" + id + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
        } else if (getDefaultValue() == null && !isNullable()) {
            throw new IllegalStateException("This variable id '" + id + "' have a nullable default value, but it doesn't supports");
        } else if (id.equalsIgnoreCase("row")) {
            throw new IllegalStateException("Illegal variable id '" + id + "'");
        }
    }

    @Contract(pure = true)
    public final @NotNull MysqlDatabase getDatabase() {
        return getTable().getDatabase();
    }

    @Contract(pure = true)
    public final @NotNull MysqlTable getTable() {
        return table;
    }

    @Contract(pure = true)
    public final @NotNull Type<T> getType() {
        return type;
    }

    public final @UnknownNullability T getDefaultValue() {
        return defaultValue;
    }

    @Contract(pure = true)
    public final boolean isNullable() {
        return nullable;
    }

    @Override
    public @NotNull CompletableFuture<Void> start() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();

        if (isLoaded()) {
            throw new IllegalStateException("The variable '" + getId() + "' is already loaded");
        } else if (connection == null) {
            throw new IllegalStateException("The variable's authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!getTable().isLoaded() || !getTable().exists().join()) {
                    throw new IllegalStateException("The table of this variable aren't loaded or created");
                }

                isNew = !exists().join();
                getType().configure(this).join();
                getTable().getVariables().add(this);

                // Sync with cache data for the receptors
                @NotNull List<MysqlData> datas = getTable().getDatas().stream().filter(Data::isLoaded).collect(Collectors.toList());
                for (MysqlData data : datas) {
                    if (isNew) {
                        data.getData().put(this, getDefaultValue());
                    } else if (data.getCache().containsKey(getId().toLowerCase())) {
                        @Nullable Object o = data.getCache().get(getId().toLowerCase());
                        data.getCache().remove(getId().toLowerCase());

                        data.getData().put(this, getType().get(o));
                    } else {
                        data.getData().put(this, getDefaultValue());
                    }
                }

                if (isNew) {
                    try (@NotNull PreparedStatement statement = connection.prepareStatement("UPDATE `" + getDatabase().getId() + "`.`" + getTable().getId() + "` SET `" + getId() + "` = ? WHERE " + SqlUtils.rowNotIn(datas.stream().map(MysqlData::getRow).collect(Collectors.toSet())))) {
                        getType().set(Parameter.of(statement, getType().isNullSupported(), 0), getDefaultValue());
                        statement.execute();
                    }
                }

                loaded = true;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> stop() {
        if (!isLoaded()) {
            throw new IllegalStateException("The variable '" + getId() + "' is not loaded");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                getTable().getVariables().remove(this);

                loaded = false;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Boolean> delete() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getDatabase().getAuthentication().getConnection().prepareStatement("ALTER TABLE `" + getDatabase().getId() + "`.`" + getTable().getId() + "` DROP COLUMN `" + getId() + "`")) {
                if (isLoaded()) {
                    stop().join();
                }

                statement.execute();

                future.complete(true);
            } catch (@NotNull Throwable throwable) {
                if (SqlUtils.getErrorCode(throwable) == 1091) {
                    future.complete(false);
                } else {
                    future.completeExceptionally(throwable);
                }
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    public @NotNull CompletableFuture<Boolean> exists() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getDatabase().getAuthentication().getConnection().prepareStatement("SELECT * FROM `" + getDatabase().getId() + "`.`" + getTable().getId() + "` LIMIT 0")) {
                @NotNull ResultSet set = statement.executeQuery();

                if (set.findColumn(getId()) > 0) {
                    future.complete(true);
                } else {
                    future.complete(false);
                }
            } catch (@NotNull Throwable throwable) {
                if (SqlUtils.getErrorCode(throwable) == 0) {
                    future.complete(false);
                } else {
                    future.completeExceptionally(throwable);
                }
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlVariable)) return false;
        if (!super.equals(object)) return false;
        MysqlVariable<?> variable = (MysqlVariable<?>) object;
        return Objects.equals(getTable(), variable.getTable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTable());
    }
}
