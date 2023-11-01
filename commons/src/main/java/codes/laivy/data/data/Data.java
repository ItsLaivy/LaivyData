package codes.laivy.data.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents an abstract data entity with common data management operations.
 * Implementations must define specific behavior for data loading, unloading, and saving.
 *
 * @since 2.0
 */
public abstract class Data {

    protected boolean isNew = false;

    @ApiStatus.Internal
    private boolean loaded = false;

    protected Data() {
    }

    public final boolean isNew() {
        if (isLoaded()) {
            return this.isNew;
        } else {
            throw new IllegalStateException("To check if the data is new, it needs to be loaded");
        }
    }

    public final @NotNull CompletableFuture<Void> reload(boolean save) {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (isLoaded()) {
                    stop(save).get(10, TimeUnit.SECONDS);
                    start().get(10, TimeUnit.SECONDS);
                }

                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public abstract @Nullable <T> T get(@NotNull String id);

    public abstract void set(@NotNull String id, @Nullable Object object);

    public final @NotNull CompletableFuture<Void> start() {
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
     * Stops the data, unloading it and marking it as not loaded, optionally saving during the process.
     *
     * @param save Whether to save before stopping
     * @return A CompletableFuture representing the asynchronous stop operation
     * @throws IllegalStateException If the data is not loaded
     * @since 2.0
     */
    public final @NotNull CompletableFuture<Void> stop(boolean save) {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                unload(save).get(10, TimeUnit.SECONDS);
                loaded = false;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    protected abstract @NotNull CompletableFuture<Void> load();

    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    protected abstract @NotNull CompletableFuture<Void> unload(boolean save);

    public abstract @NotNull CompletableFuture<Void> save();

    public boolean isLoaded() {
        return loaded;
    }
}
