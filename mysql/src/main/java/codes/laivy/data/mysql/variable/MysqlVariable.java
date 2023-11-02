package codes.laivy.data.mysql.variable;

import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.utils.SqlUtils;
import codes.laivy.data.mysql.variable.type.Type;
import codes.laivy.data.variable.Variable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class MysqlVariable<T> extends Variable<T> {

    private final @NotNull MysqlTable table;
    private final @NotNull Type<T> type;

    private final @Nullable T defaultValue;

    public MysqlVariable(@NotNull String id, @NotNull MysqlTable table, @NotNull Type<T> type, @Nullable T defaultValue) {
        super(id);

        this.table = table;
        this.type = type;
        this.defaultValue = defaultValue;

        if (!id.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This variable id '" + id + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
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

    public final @Nullable T getDefaultValue() {
        return defaultValue;
    }

    @Override
    protected @NotNull CompletableFuture<Void> load() {
        if (getDatabase().getAuthentication().getConnection() == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                isNew = !exists().join();
                getType().configure(this).join();
                getTable().getVariables().add(this);

                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @Override
    protected @NotNull CompletableFuture<Void> unload() {
        return CompletableFuture.runAsync(() -> {
            getTable().getVariables().remove(this);
        });
    }

    @Override
    public @NotNull CompletableFuture<Boolean> delete() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getDatabase().getAuthentication().getConnection().prepareStatement("ALTER TABLE `" + getDatabase().getId() + "`.`" + getTable().getName() + "` DROP COLUMN `" + getId() + "`")) {
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
        });

        return future;
    }

    public @NotNull CompletableFuture<Boolean> exists() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getDatabase().getAuthentication().getConnection().prepareStatement("SELECT * FROM `" + getDatabase().getId() + "`.`" + getTable().getName() + "` LIMIT 0")) {
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
        });

        return future;
    }
}
