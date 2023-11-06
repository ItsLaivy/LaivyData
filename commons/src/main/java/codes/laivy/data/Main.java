package codes.laivy.data;

import codes.laivy.data.api.Api;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class Main {

    public static @NotNull Api api = new Api() {

        public final @NotNull Executor executor = (ForkJoinPool.getCommonPoolParallelism() > 1) ? ForkJoinPool.commonPool() : command -> new Thread(command).start();

        @Override
        public @NotNull Executor getExecutor(@NotNull Class<?> clasz) {
            return executor;
        }
    };

    public static @NotNull Executor getExecutor(@NotNull Class<?> clasz) {
        return api.getExecutor(clasz);
    }

    public static void main(String[] args) {
    }
}