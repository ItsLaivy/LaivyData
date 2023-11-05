package codes.laivy.data.mysql;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class Main {

    public static @NotNull Executor MAIN_EXECUTOR = new ForkJoinPool();

    public static void main(String[] args) {
    }
}