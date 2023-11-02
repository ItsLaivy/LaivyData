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
    protected boolean loaded = false;

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

    public abstract @Nullable Object get(@NotNull String id);

    public abstract void set(@NotNull String id, @Nullable Object object);

    public @NotNull CompletableFuture<Void> start() {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                loaded = true;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public @NotNull CompletableFuture<Void> stop(boolean save) {
        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                loaded = false;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public abstract @NotNull CompletableFuture<Void> save();

    public boolean isLoaded() {
        return loaded;
    }
}
