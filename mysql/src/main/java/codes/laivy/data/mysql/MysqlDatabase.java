package codes.laivy.data.mysql;

import codes.laivy.data.Database;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MysqlDatabase extends Database {

    /**
     * Retrieves an active database in this authentication,
     * or returns an empty Optional if no database with the specified id exists.
     *
     * @param authentication The MySQL authentication
     * @param id The database id
     * @return An Optional containing the MysqlDatabase if found, else an empty Optional
     */
    public static @NotNull Optional<MysqlDatabase> get(@NotNull MysqlAuthentication authentication, @NotNull String id) {
        if (authentication.databases == null) {
            throw new IllegalStateException("This authentication is not connected");
        }

        return authentication.databases.stream()
                .filter(db -> db.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    public static @NotNull MysqlDatabase getOrCreate(@NotNull MysqlAuthentication authentication, @NotNull String id) {
        if (authentication.databases == null) {
            throw new IllegalStateException("This authentication is not connected");
        }

        @NotNull Optional<MysqlDatabase> databaseOptional = get(authentication, id);
        if (databaseOptional.isPresent()) {
            return databaseOptional.get();
        } else {
            @NotNull MysqlDatabase database = new MysqlDatabase(authentication, id);
            authentication.databases.add(database);
            return database;
        }
    }

    private final @NotNull MysqlAuthentication authentication;

    private @Nullable Set<MysqlTable> tables;

    /**
     * Constructs a MysqlDatabase instance with the specified id.
     *
     * @param authentication The authentication of this database
     * @param id The unique id of the database
     * @throws UnsupportedOperationException If the id does not match the pattern
     * @since 1.0
     */
    protected MysqlDatabase(@NotNull MysqlAuthentication authentication, @NotNull String id) {
        super(id);
        this.authentication = authentication;

        if (!id.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This database id '" + id + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
        }
    }

    @Contract(pure = true)
    public final @NotNull MysqlAuthentication getAuthentication() {
        return authentication;
    }

    @Override
    protected @NotNull CompletableFuture<Void> load() {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!exists().get(5, TimeUnit.SECONDS)) {
                    create().get(5, TimeUnit.SECONDS);
                }

                tables = new LinkedHashSet<>();
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
                tables = null;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public final @NotNull CompletableFuture<Void> create() {
        if (getAuthentication().getConnection() == null) {
            throw new IllegalStateException("This authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getAuthentication().getConnection().prepareStatement("CREATE DATABASE IF NOT EXISTS " + getId())) {
                statement.execute();
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
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
                statement.execute();
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> save() {
        return null;
    }

    @Override
    @Contract(pure = true)
    public final boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlDatabase)) return false;
        if (!super.equals(object)) return false;
        MysqlDatabase that = (MysqlDatabase) object;
        return Objects.equals(getAuthentication(), that.getAuthentication());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(super.hashCode(), getAuthentication());
    }

    @Override
    public @NotNull String toString() {
        return "MysqlDatabase{" +
                "authentication=" + authentication +
                '}';
    }

    public final @NotNull CompletableFuture<Boolean> exists() throws SQLException {
        if (getAuthentication().getConnection() == null) {
            throw new IllegalStateException("This authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getAuthentication().getConnection().prepareStatement("SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?;")) {
                statement.setString(1, getId());
                @NotNull ResultSet set = statement.executeQuery();
                future.complete(set.next());
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }
}
