package codes.laivy.data.variable;

import codes.laivy.data.Database;
import codes.laivy.data.data.Receptor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Represents an abstract variable, acting as a container for values.
 *
 * @param <T> The type of the variable
 * @author Laivy
 * @since 1.0
 */
public abstract class Variable<T> {

    private final @NotNull String id;
    private final @NotNull Database database;

    protected volatile boolean isNew = false;

    @ApiStatus.Internal
    private volatile boolean loaded = false;

    /**
     * Constructs a Variable instance with the specified id and associated database.
     *
     * @param id       The unique id of the variable
     * @param database The database instance
     * @since 1.0
     */
    public Variable(@NotNull String id, @NotNull Database database) {
        this.id = id;
        this.database = database;
    }

    /**
     * Returns the function responsible for collecting default values for a receptor.
     *
     * @return A function that collects default values for the receptor
     * @since 2.0
     */
    public abstract @NotNull Function<? super Receptor, T> getDefaultValue();

    /**
     * Gets the id of the variable.
     * <p>
     * The id of variables must follow the database regexes; not all characters will be allowed here.
     *
     * @return The variable id
     * @since 1.0
     */
    @Contract(pure = true)
    public final @NotNull String getId() {
        return this.id;
    }

    /**
     * Gets the database associated with the variable.
     *
     * @return The database
     * @since 1.0
     */
    @Contract(pure = true)
    public final @NotNull Database getDatabase() {
        return this.database;
    }

    /**
     * Starts the variable.
     *
     * @return A CompletableFuture representing the asynchronous start operation
     * @throws IllegalStateException If the variable is already loaded
     * @since 2.0
     */
    public final @NotNull CompletableFuture<Void> start() {
        if (isLoaded()) {
            throw new IllegalStateException("The variable '" + getId() + "' is already loaded");
        }

        return CompletableFuture.runAsync(() -> {
            load().join();
            loaded = true;
        });
    }

    /**
     * Stops the variable.
     *
     * @return A CompletableFuture representing the asynchronous stop operation
     * @throws IllegalStateException If the variable is not loaded
     * @since 2.0
     */
    public final @NotNull CompletableFuture<Void> stop() {
        if (!isLoaded()) {
            throw new IllegalStateException("The variable '" + getId() + "' is not loaded");
        }

        return CompletableFuture.runAsync(() -> {
            unload().join();
            loaded = false;
        });
    }

    /**
     * Checks if the variable is new.
     *
     * @return True if the variable is new, false otherwise
     * @throws IllegalStateException If the variable is not loaded
     * @since 2.0
     */
    public final boolean isNew() {
        if (isLoaded()) {
            return this.isNew;
        } else {
            throw new IllegalStateException("To check if the variable is new, it needs to be loaded");
        }
    }

    /**
     * Gets the VariableType responsible for controlling the value of this variable.
     *
     * @return The variable's type controller
     * @since 1.0
     */
    @Contract(pure = true)
    public abstract @NotNull VariableType<T> getType();

    /**
     * Loads the variable from the database.
     * You cannot load if a variable is loaded at this database with this same id.
     *
     * @return A CompletableFuture representing the asynchronous load operation
     * @since 1.0
     */
    @ApiStatus.OverrideOnly
    protected abstract @NotNull CompletableFuture<Void> load();

    /**
     * Unloads the variable and all its components from the database.
     *
     * @return A CompletableFuture representing the asynchronous unload operation
     * @since 1.0
     */
    @ApiStatus.OverrideOnly
    protected abstract @NotNull CompletableFuture<Void> unload();

    /**
     * Deletes the variable from the database.
     *
     * @return A CompletableFuture representing the asynchronous delete operation
     * @since 1.0
     */
    public abstract @NotNull CompletableFuture<Void> delete();

    /**
     * Checks if this variable is loaded or not.
     *
     * @return True if the variable is loaded
     * @since 1.0
     */
    public final boolean isLoaded() {
        return loaded;
    }
}
