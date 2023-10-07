package codes.laivy.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

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

        if (!getPattern().matcher(id).matches()) {
            throw new UnsupportedOperationException("The database id '" + id + "' doesn't matches the regex '" + getPattern().pattern() + "'");
        }
    }

    /**
     * Gets the main pattern used for validating database ids.
     *
     * @return The pattern for id validation
     * @since 2.0
     */
    @Contract(pure = true)
    protected abstract @NotNull Pattern getPattern();

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

        return CompletableFuture.runAsync(() -> {
            load().join();
            loaded = true;
        });
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

        return CompletableFuture.runAsync(() -> {
            unload().join();
            loaded = false;
        });
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
    protected abstract @NotNull CompletableFuture<Void> load();

    /**
     * Unloads the database, releasing resources.
     *
     * @return A CompletableFuture representing the asynchronous unload operation
     * @since 1.0
     */
    protected abstract @NotNull CompletableFuture<Void> unload();

    /**
     * Deletes the database and its contents.
     *
     * @return A CompletableFuture representing the asynchronous delete operation
     * @since 1.0
     */
    public abstract @NotNull CompletableFuture<Void> delete();

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
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
