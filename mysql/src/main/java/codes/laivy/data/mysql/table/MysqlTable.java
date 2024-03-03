package codes.laivy.data.mysql.table;

import codes.laivy.data.Main;
import codes.laivy.data.mysql.data.MysqlData;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.utils.SqlUtils;
import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MysqlTable {

    private final @NotNull String id;
    private final @NotNull MysqlDatabase database;

    private final @NotNull Variables variables;
    private final @NotNull DataContent dataContent;

    private final @NotNull AutoIncrement autoIncrement;

    protected boolean isNew = false;

    @ApiStatus.Internal
    protected boolean loaded = false;

    protected MysqlTable(@NotNull String id, @NotNull MysqlDatabase database, @NotNull Variables variables, @NotNull DataContent dataContent, @NotNull AutoIncrement autoIncrement) {
        this.id = id;
        this.database = database;
        this.variables = variables;
        this.dataContent = dataContent;
        this.autoIncrement = autoIncrement;

        if (!id.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This table name '" + id + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
        }
    }
    public MysqlTable(@NotNull String id, @NotNull MysqlDatabase database) {
        this.id = id;
        this.database = database;

        this.variables = new Variables(this);
        this.dataContent = new DataContent(this);
        this.autoIncrement = AutoIncrement.of(this);

        if (!id.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This table name '" + id + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
        }
    }

    public final @NotNull CompletableFuture<Void> start() {
        if (isLoaded()) {
            throw new IllegalStateException("The mysql table '" + getId() + "' is already loaded");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!exists().join()) {
                    isNew = true;
                    create().join();
                } else {
                    isNew = false;
                }

                getDatabase().getTables().add(this);

                loaded = true;

                for (MysqlVariable<?> variable : getVariables().getDefault()) {
                    variable.start().join();
                }

                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }
    public final @NotNull CompletableFuture<Void> stop() {
        if (!isLoaded()) {
            throw new IllegalStateException("The mysql table '" + getId() + "' is not loaded");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                for (@NotNull MysqlData data : new HashSet<>(getDataContent().toCollection())) {
                    if (data.isLoaded()) {
                        data.stop(true).join();
                    }
                }
                for (MysqlVariable<?> variable : new HashSet<>(getVariables().toCollection())) {
                    if (variable.isLoaded()) {
                        variable.stop().join();
                    }
                }

                getDataContent().clear();
                getVariables().clear();

                getDatabase().getTables().remove(this);

                loaded = false;
                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    public @NotNull CompletableFuture<Boolean> create() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getDatabase().getAuthentication().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `" + getDatabase().getId() + "`.`" + getId() + "` (`row` INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY);")) {
                statement.execute();

                future.complete(true);
            } catch (@NotNull Throwable throwable) {
                if (SqlUtils.getErrorCode(throwable) == 1051) {
                    future.complete(false);
                } else {
                    future.completeExceptionally(throwable);
                }
            }
        }, Main.getExecutor(getClass()));

        return future;
    }
    public @NotNull CompletableFuture<Boolean> delete() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getDatabase().getAuthentication().getConnection().prepareStatement("DROP TABLE `" + getDatabase().getId() + "`.`" + getId() + "`")) {
                if (isLoaded()) {
                    stop().join();
                }

                statement.execute();

                future.complete(true);
            } catch (@NotNull Throwable throwable) {
                if (SqlUtils.getErrorCode(throwable) == 1051) {
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
            try {
                boolean databaseExists = getDatabase().exists().join();

                if (databaseExists) {
                    try (@NotNull ResultSet resultSet = connection.getMetaData().getTables(getDatabase().getId(), null, getId(), null)) {
                        future.complete(resultSet.next());
                        return;
                    }
                }

                future.complete(false);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Contract(pure = true)
    public final @NotNull AutoIncrement getAutoIncrement() {
        return autoIncrement;
    }

    @Contract(pure = true)
    public final @NotNull String getId() {
        return id;
    }

    @Contract(pure = true)
    public final @NotNull MysqlDatabase getDatabase() {
        return database;
    }

    public final @NotNull Variables getVariables() {
        return variables;
    }
    public final @NotNull DataContent getDataContent() {
        return dataContent;
    }

    public @NotNull CompletableFuture<Long> getRows() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Long> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                try (@NotNull PreparedStatement statement = connection.prepareStatement("SHOW TABLE STATUS FROM `" + getDatabase().getId() + "` LIKE '" + getId() + "'")) {
                    @NotNull ResultSet set = statement.executeQuery();
                    set.next();

                    future.complete(set.getLong("rows"));
                }
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    public final boolean isNew() {
        if (isLoaded()) {
            return this.isNew;
        } else {
            throw new IllegalStateException("To check if the table is new, it needs to be loaded");
        }
    }

    public final boolean isLoaded() {
        return loaded;
    }

    @Override
    @Contract(pure = true)
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlTable)) return false;
        MysqlTable that = (MysqlTable) object;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getDatabase(), that.getDatabase());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId(), getDatabase());
    }

    @Override
    public @NotNull String toString() {
        return "MysqlTable{" +
                "id='" + id + '\'' +
                ", database=" + database +
                ", variables=" + variables +
                '}';
    }
}
