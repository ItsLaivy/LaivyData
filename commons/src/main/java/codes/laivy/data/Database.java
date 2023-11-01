package codes.laivy.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents an abstract database responsible for storing variables and their dependencies.
 * Typically, the database does not hold the connection object or reference; that is managed by the database manager.
 *
 * @author Laivy
 * @since 1.0
 */
public abstract class Database {

    private final @NotNull String id;

    protected boolean isNew = false;

    @ApiStatus.Internal
    private volatile boolean loaded = false;

    /**
     * Constructs a Database instance with the specified id.
     *
     * @param id The unique id of the database
     * @throws UnsupportedOperationException If the id does not match the pattern
     * @since 1.0
     */
    protected Database(@NotNull String id) {
        this.id = id;
    }

    /**
     * Gets the unique id of the database.
     *
     * @return The unique id of the database
     * @since 1.0
     */
    @Contract(pure = true)
    public final @NotNull String getId() {
        return this.id;
    }

    /**
     * Starts the database.
     *
     * @return A CompletableFuture representing the asynchronous start operation
     * @throws IllegalStateException If the database is already loaded
     * @since 2.0
     */
    public final @NotNull CompletableFuture<Void> start() {
        if (isLoaded()) {
            throw new IllegalStateException("The database '" + getId() + "' is already loaded");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                load().get(10, TimeUnit.SECONDS);
                loaded = true;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    /**
     * Stops the database.
     *
     * @return A CompletableFuture representing the asynchronous stop operation
     * @throws IllegalStateException If the database is not loaded
     * @since 2.0
     */
    public final @NotNull CompletableFuture<Void> stop() {
        if (!isLoaded()) {
            throw new IllegalStateException("The database '" + getId() + "' is not loaded");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                unload().get(10, TimeUnit.SECONDS);
                loaded = false;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    /**
     * Checks if the database is new.
     *
     * @return True if the database is new, false otherwise
     * @throws IllegalStateException If the database is not loaded
     * @since 2.0
     */
    public final boolean isNew() {
        if (isLoaded()) {
            return this.isNew;
        } else {
            throw new IllegalStateException("To check if the database is new, it needs to be loaded");
        }
    }

    /**
     * Loads the database, preparing it for operations.
     *
     * @return A CompletableFuture representing the asynchronous load operation
     * @since 1.0
     */
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    protected abstract @NotNull CompletableFuture<Void> load();

    /**
     * Unloads the database, releasing resources.
     *
     * @return A CompletableFuture representing the asynchronous unload operation
     * @since 1.0
     */
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    protected abstract @NotNull CompletableFuture<Void> unload();

    /**
     * Deletes the database and its contents.
     *
     * @return A CompletableFuture representing the asynchronous delete operation
     * @since 1.0
     */
    public abstract @NotNull CompletableFuture<Void> delete();

    // TODO: 07/10/2023 Javadocs
    public abstract @NotNull CompletableFuture<Void> save();

    /**
     * Checks if the database is loaded.
     *
     * @return True if the database is loaded, false otherwise
     * @since 1.0
     */
    public final boolean isLoaded() {
        return loaded;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Database)) return false;
        Database database = (Database) object;
        return Objects.equals(getId(), database.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public @NotNull String toString() {
        return "Database{" +
                "id='" + id + '\'' +
                ", isNew=" + isNew +
                ", loaded=" + loaded +
                '}';
    }
}
