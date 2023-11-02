package codes.laivy.data.variable;

import codes.laivy.data.data.Receptor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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

    protected boolean isNew = false;

    @ApiStatus.Internal
    private boolean loaded = false;

    /**
     * Constructs a Variable instance with the specified id and associated database.
     *
     * @param id       The unique id of the variable
     * @since 1.0
     */
    protected Variable(@NotNull String id) {
        this.id = id;
    }

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

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                load().join();
                loaded = true;

                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
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
     * Loads the variable from the database.
     * You cannot load if a variable is loaded at this database with this same id.
     *
     * @return A CompletableFuture representing the asynchronous load operation
     * @since 1.0
     */
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    protected abstract @NotNull CompletableFuture<Void> load();

    /**
     * Unloads the variable and all its components from the database.
     *
     * @return A CompletableFuture representing the asynchronous unload operation
     * @since 1.0
     */
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    protected abstract @NotNull CompletableFuture<Void> unload();

    /**
     * Deletes the variable from the database.
     * The future will return true if the variable has successfully deleted, false otherwise.
     *
     * @return A CompletableFuture representing the asynchronous delete operation
     * @since 1.0
     */
    public abstract @NotNull CompletableFuture<Boolean> delete();

    /**
     * Checks if this variable is loaded or not.
     *
     * @return True if the variable is loaded
     * @since 1.0
     */
    public final boolean isLoaded() {
        return loaded;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Variable)) return false;
        Variable<?> variable = (Variable<?>) object;
        return Objects.equals(getId(), variable.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public @NotNull String toString() {
        return "Variable{" +
                "id='" + id + '\'' +
                ", isNew=" + isNew +
                ", loaded=" + loaded +
                '}';
    }
}
