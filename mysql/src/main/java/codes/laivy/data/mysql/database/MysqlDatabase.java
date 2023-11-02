package codes.laivy.data.mysql.database;

import codes.laivy.data.Database;
import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.data.MysqlData;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.utils.SqlUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class MysqlDatabase extends Database {

    /**
     * Retrieves an active database in this authentication,
     * or returns an empty Optional if no database with the specified id exists.
     *
     * @param authentication The MySQL authentication
     * @param id The database id
     * @return An Optional containing the MysqlDatabase if found, else an empty Optional
     */
    public static @NotNull Optional<MysqlDatabase> get(@NotNull MysqlAuthentication authentication, @NotNull String id) {
        if (!authentication.isConnected()) {
            throw new IllegalStateException("This authentication is not connected");
        }

        return authentication.getDatabases().stream()
                .filter(db -> db.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    public static @NotNull MysqlDatabase getOrCreate(@NotNull MysqlAuthentication authentication, @NotNull String id) {
        if (!authentication.isConnected()) {
            throw new IllegalStateException("This authentication is not connected");
        }

        @NotNull Optional<MysqlDatabase> databaseOptional = get(authentication, id);
        if (databaseOptional.isPresent()) {
            return databaseOptional.get();
        } else {
            @NotNull MysqlDatabase database = new MysqlDatabase(authentication, id);
            authentication.getDatabases().add(database);
            return database;
        }
    }

    private final @NotNull MysqlAuthentication authentication;
    private final @NotNull Tables tables;

    /**
     * Constructs a MysqlDatabase instance with the specified id.
     *
     * @param authentication The authentication of this database
     * @param id The unique id of the database
     * @throws UnsupportedOperationException If the id does not match the pattern
     * @since 1.0
     */
    private MysqlDatabase(@NotNull MysqlAuthentication authentication, @NotNull String id) {
        super(id);

        this.authentication = authentication;
        this.tables = new Tables(this);

        if (!id.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This database id '" + id + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
        }
    }

    public @NotNull MysqlAuthentication getAuthentication() {
        return authentication;
    }

    public @NotNull Tables getTables() {
        return tables;
    }

    int loadedtimes = 0;

    @Override
    public @NotNull CompletableFuture<Boolean> start() {
        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (isLoaded()) {
            return CompletableFuture.completedFuture(false);
        }

        CompletableFuture.runAsync(() -> {
            try {
                if (!exists().join()) create().join();

                load().join();

                loaded = true;
                future.complete(true);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

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

                for (MysqlTable table : getTables()) {
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
        });

        return future;
    }

    @Override
    protected @NotNull CompletableFuture<Void> load() {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @Override
    protected @NotNull CompletableFuture<Void> unload() {
        if (getAuthentication().getConnection() == null) {
            throw new IllegalStateException("This authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public @NotNull CompletableFuture<Boolean> create() {
        if (getAuthentication().getConnection() == null) {
            throw new IllegalStateException("This authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getAuthentication().getConnection().prepareStatement("CREATE DATABASE IF NOT EXISTS " + getId())) {
                statement.execute();

                if (!getAuthentication().getDatabases().contains(this)) {
                    getAuthentication().getDatabases().add(this);
                }

                future.complete(true);
            } catch (Throwable throwable) {
                if (SqlUtils.getErrorCode(throwable) == 1007) {
                    future.complete(false);
                } else {
                    future.completeExceptionally(throwable);
                }
            }
        });

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> delete() {
        if (getAuthentication().getConnection() == null) {
            throw new IllegalStateException("This authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getAuthentication().getConnection().prepareStatement("DROP DATABASE " + getId())) {
                if (isLoaded()) {
                    stop().join();
                }

                statement.execute();
                getAuthentication().getDatabases().remove(this);

                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> save() {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            for (MysqlTable table : getTables()) {
                if (table.isLoaded()) {
                    for (MysqlData data : table.getDatas()) {
                        data.save().join();
                    }
                }
            }
        });

        return future;
    }

    @Override
    @Contract(pure = true)
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlDatabase)) return false;
        if (!super.equals(object)) return false;
        MysqlDatabase that = (MysqlDatabase) object;
        return Objects.equals(getAuthentication(), that.getAuthentication());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAuthentication());
    }

    @Override
    public @NotNull String toString() {
        return "MysqlDatabase{" +
                "authentication=" + authentication +
                '}';
    }

    public @NotNull CompletableFuture<Boolean> exists() throws SQLException {
        @Nullable Connection connection = getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (@NotNull ResultSet resultSet = connection.getMetaData().getCatalogs()) {
                while (resultSet.next()) {
                    @NotNull String databaseName = resultSet.getString(1);
                    if (databaseName.equalsIgnoreCase(getId())) {
                        future.complete(true);
                        return;
                    }
                }

                future.complete(false);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }
}
