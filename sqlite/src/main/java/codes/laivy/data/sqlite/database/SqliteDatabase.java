package codes.laivy.data.sqlite.database;

import codes.laivy.data.Database;
import codes.laivy.data.Main;
import codes.laivy.data.sqlite.SqliteData;
import codes.laivy.data.sqlite.table.SqliteTable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.File;
import java.nio.file.FileSystemException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class SqliteDatabase extends Database {

    private static final @NotNull List<SqliteDatabase> databases = new ArrayList<>();

    /**
     * Retrieves an active database with the specified file,
     * or returns an empty Optional if no database in the specified file exists.
     *
     * @param file The Sqlite database file
     * @return An Optional containing the SqliteDatabase if found, else an empty Optional
     */
    public static @NotNull Optional<SqliteDatabase> get(@NotNull File file) {
        return databases.stream().filter(database -> database.getFile().equals(file)).findFirst();
    }

    public static @NotNull SqliteDatabase getOrCreate(@NotNull File file) {
        @NotNull Optional<SqliteDatabase> databaseOptional = get(file);
        return databaseOptional.orElseGet(() -> new SqliteDatabase(file));
    }

    // Object

    private final @NotNull File file;
    private final @NotNull Tables tables;

    private @UnknownNullability Connection connection;

    /**
     * Constructs a SqliteDatabase instance with the specified id.
     *
     * @param file The sqlite file of this database
     * @throws UnsupportedOperationException If the id does not match the pattern
     * @since 1.0
     */
    private SqliteDatabase(@NotNull File file) {
        super(file.getName());

        if (file.isDirectory()) {
            throw new IllegalArgumentException("sqlite database path must be a valid file: " + file);
        }

        this.file = file;
        this.tables = new Tables(this);

        databases.add(this);
    }

    public @NotNull File getFile() {
        return file;
    }
    public @NotNull Tables getTables() {
        return tables;
    }

    public @Nullable Connection getConnection() {
        return connection;
    }

    @Override
    public @NotNull CompletableFuture<Boolean> start() {
        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (isLoaded()) {
            return CompletableFuture.completedFuture(false);
        }

        CompletableFuture.runAsync(() -> {
            try {
                if (!exists().join()) {
                    isNew = true;
                    create().join();
                } else {
                    isNew = false;
                }

                load().join();

                loaded = true;
                future.complete(true);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> stop() {
        if (!isLoaded()) {
            throw new IllegalStateException("The database '" + getId() + "' is not loaded");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                unload().join();

                for (SqliteTable table : new HashSet<>(getTables().toCollection())) {
                    if (table.isLoaded()) {
                        table.stop().join();
                    }
                }
                getTables().clear();

                loaded = false;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Override
    protected @NotNull CompletableFuture<Void> load() throws Exception {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!exists().get() && !create().get()) {
                    throw new IllegalStateException("cannot create sqlite database file: " + getFile());
                }

                connection = DriverManager.getConnection("jdbc:sqlite:" + getFile());
                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Override
    protected @NotNull CompletableFuture<Void> unload() {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!getFile().exists() && !create().get()) {
                    throw new IllegalStateException("cannot create sqlite database file: " + getFile());
                }

                connection.close();
                connection = null;

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
                if (exists().get()) {
                    throw new IllegalStateException("file already exists");
                }

                @NotNull File file = getFile();
                if (!file.exists()) {
                    if (!file.getParentFile().mkdirs() & !file.createNewFile()) {
                        throw new IllegalStateException("cannot create '" + file + "' folder");
                    }

                    future.complete(true);
                }

                future.complete(false);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> delete() {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (isLoaded()) {
                    stop().get();
                }

                if (exists().join()) {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }

                    if (!getFile().delete()) {
                        throw new FileSystemException(file.getName(), null, "cannot delete sqlite database file");
                    }
                }

                databases.remove(this);
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> save() {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            for (SqliteTable table : getTables()) {
                if (table.isLoaded()) {
                    for (SqliteData data : table.getDataContent()) {
                        data.save().join();
                    }
                }
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    public @NotNull CompletableFuture<Boolean> exists() throws SQLException {
        return CompletableFuture.completedFuture(connection != null || getFile().exists());
    }

    @Override
    @Contract(pure = true)
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof SqliteDatabase)) return false;
        if (!super.equals(object)) return false;
        SqliteDatabase that = (SqliteDatabase) object;
        return Objects.equals(getFile(), that.getFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFile());
    }

    @Override
    public @NotNull String toString() {
        return "SqliteDatabase{" +
                "file=" + file +
                '}';
    }

}
