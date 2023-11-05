package codes.laivy.data.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public interface Api {

    @NotNull Executor getExecutor(@NotNull Class<?> clasz);

}
