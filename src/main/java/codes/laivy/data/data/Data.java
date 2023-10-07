package codes.laivy.data.data;

import codes.laivy.data.Database;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an abstract data entity with common data management operations.
 * Implementations must define specific behavior for data loading, unloading, and saving.
 *
 * @since 2.0
 */
public abstract class Data {

    private final @NotNull Database database;

    // TODO: 07/10/2023 Remove this, add only on SqlData
    protected volatile long row = -1;

    protected volatile boolean isNew = false;

    @ApiStatus.Internal
    private volatile boolean loaded = false;

    /**
     * Constructs a Data instance associated with the specified database.
     *
     * @param database The database instance
     * @since 1.0
     */
    public Data(@NotNull Database database) {
        this.database = database;
    }

    /**
     * Gets the row number associated with this data at the database incrementation.
     *
     * @return The row number
     * @throws IllegalStateException If the data is not loaded
     * @since 2.0
     */
    public final long getRow() {
        if (row != -1) {
            return row;
        } else {
            throw new IllegalStateException("To obtain the row value, the data needs to be loaded");
        }
    }

    /**
     * Gets the database associated with this data.
     *
     * @return The database instance
     * @since 1.0
     */
    @Contract(pure = true)
    public final @NotNull Database getDatabase() {
        return this.database;
    }

    /**
     * Checks if the data is new, i.e., created before being loaded into the database.
     *
     * @return True if the data is new, false otherwise
     * @throws IllegalStateException If the data is not loaded
     * @since 1.0
     */
    public final boolean isNew() {
        if (isLoaded()) {
            return this.isNew;
        } else {
            throw new IllegalStateException("To check if the data is new, it needs to be loaded");
        }
    }

    /**
     * Reloads the data by unloading and loading again, optionally saving during the process.
     *
     * @param save Whether to save during the reloading process
     * @since 2.0
     */
    public final @NotNull CompletableFuture<Void> reload(boolean save) {
        return CompletableFuture.runAsync(() -> {
            if (isLoaded()) {
                stop(save).join();
                start().join();
            }
        });
    }

    /**
     * Gets the value of a variable with the specified ID, processed by the associated VariableType.
     *
     * @param id The ID of the variable
     * @return The object associated with the variable
     * @since 1.0
     */
    public abstract @Nullable <T> T get(@NotNull String id);

    /**
     * Sets the value of a variable with the specified ID, processed by the associated VariableType.
     *
     * @param id     The ID of the variable
     * @param object The object to set as the variable value
     * @since 1.0
     */
    public abstract void set(@NotNull String id, @Nullable Object object);

    /**
     * Starts the data, loading it and marking it as loaded.
     *
     * @return A CompletableFuture representing the asynchronous start operation
     * @throws IllegalStateException If the data is already loaded
     * @since 2.0
     */
    public final @NotNull CompletableFuture<Void> start() {
        return CompletableFuture.runAsync(() -> {
            row = load().join();

            if (row < 0) {
                throw new IllegalStateException("Invalid row value from #load method");
            }

            loaded = true;
        });
    }

    /**
     * Stops the data, unloading it and marking it as not loaded, optionally saving during the process.
     *
     * @param save Whether to save before stopping
     * @return A CompletableFuture representing the asynchronous stop operation
     * @throws IllegalStateException If the data is not loaded
     * @since 2.0
     */
    public final @NotNull CompletableFuture<Void> stop(boolean save) {
        return CompletableFuture.runAsync(() -> {
            unload(save).join();
            row = -1;
            loaded = false;
        });
    }

    /**
     * Loads the data from the database and retrieves variable values.
     * A data instance cannot be loaded if another instance with the same ID in this database is already loaded.
     *
     * @return A CompletableFuture providing the row associated with this data
     * @since 1.0
     */
    @ApiStatus.OverrideOnly
    protected abstract @NotNull CompletableFuture<Long> load();

    /**
     * Unloads the data from the database and removes all data from memory.
     *
     * @param save Whether to save before unloading
     * @return A CompletableFuture representing the asynchronous unload operation
     * @since 1.0
     */
    @ApiStatus.OverrideOnly
    protected abstract @NotNull CompletableFuture<Void> unload(boolean save);

    /**
     * Saves the receptor variables and values into the database.
     *
     * @return A CompletableFuture representing the asynchronous save operation
     * @since 1.0
     */
    public abstract @NotNull CompletableFuture<Void> save();

    /**
     * Checks if the data is loaded.
     *
     * @return True if the data is loaded, false otherwise
     * @since 1.0
     */
    public final boolean isLoaded() {
        return loaded && row != -1;
    }
}
