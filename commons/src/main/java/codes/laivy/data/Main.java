package codes.laivy.data;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static @NotNull Executor MAIN_EXECUTOR = new ForkJoinPool();

    public static void main(String[] args) {
    }

}
