package codes.laivy.data.sqlite.table;

import codes.laivy.data.Main;
import codes.laivy.data.sqlite.SqliteData;
import codes.laivy.data.sqlite.database.SqliteDatabase;
import codes.laivy.data.sqlite.utils.SqlUtils;
import codes.laivy.data.sqlite.variable.SqliteVariable;
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

public class SqliteTable {

    private final @NotNull String id;
    private final @NotNull SqliteDatabase database;

    private final @NotNull Variables variables;
    private final @NotNull DataContent dataContent;

    private final @NotNull AutoIncrement autoIncrement;

    protected boolean isNew = false;

    @ApiStatus.Internal
    protected boolean loaded = false;

    protected SqliteTable(@NotNull String id, @NotNull SqliteDatabase database, @NotNull Variables variables, @NotNull DataContent dataContent, @NotNull AutoIncrement autoIncrement) {
        this.id = id;
        this.database = database;
        this.variables = variables;
        this.dataContent = dataContent;
        this.autoIncrement = autoIncrement;

        if (!id.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This table name '" + id + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
        }
    }
    public SqliteTable(@NotNull String id, @NotNull SqliteDatabase database) {
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

                for (SqliteVariable<?> variable : getVariables().getDefault()) {
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
                for (@NotNull SqliteData data : new HashSet<>(getDataContent().toCollection())) {
                    if (data.isLoaded()) {
                        data.stop(true).join();
                    }
                }
                for (SqliteVariable<?> variable : new HashSet<>(getVariables().toCollection())) {
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
        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @Nullable Connection connection = getDatabase().getConnection();
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("The database's authentication aren't connected");
                }

                try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS '" + getId() + "' (row INTEGER PRIMARY KEY AUTOINCREMENT);")) {
                    statement.execute();

                    future.complete(true);
                }
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
        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @Nullable Connection connection = getDatabase().getConnection();
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("The database's authentication aren't connected");
                }

                try (PreparedStatement statement = connection.prepareStatement("DROP TABLE IF EXISTS '" + getId() + "'")) {
                    if (isLoaded()) {
                        stop().join();
                    }

                    statement.execute();

                    future.complete(true);
                }
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }
    public @NotNull CompletableFuture<Boolean> exists() {
        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @Nullable Connection connection = getDatabase().getConnection();
                if (!getDatabase().exists().get()) {
                    future.complete(false);
                } else if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("the database aren't connected");
                } else {
                    boolean databaseExists = getDatabase().exists().join();

                    if (databaseExists) {
                        try (@NotNull ResultSet resultSet = connection.getMetaData().getTables(getDatabase().getId(), null, getId(), null)) {
                            future.complete(resultSet.next());
                            return;
                        }
                    }

                    future.complete(false);
                }
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
    public final @NotNull SqliteDatabase getDatabase() {
        return database;
    }

    public final @NotNull Variables getVariables() {
        return variables;
    }
    public final @NotNull DataContent getDataContent() {
        return dataContent;
    }

    // todo: table rows
    public @NotNull CompletableFuture<Long> getRows() {
        @NotNull CompletableFuture<Long> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @Nullable Connection connection = getDatabase().getConnection();
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("The database's authentication aren't connected");
                }

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
        if (!(object instanceof SqliteTable)) return false;
        SqliteTable that = (SqliteTable) object;
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
