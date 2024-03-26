package codes.laivy.data.sqlite.table;

import codes.laivy.data.Main;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public interface AutoIncrement {

    default @NotNull CompletableFuture<Integer> getAndIncrement(int increment) {
        throw new UnsupportedOperationException("cannot change sqlite table auto increment values");
    }

    @NotNull CompletableFuture<Integer> getAmount();

    @NotNull CompletableFuture<Void> setAmount(int value);

    // Static initializers

    @ApiStatus.Internal
    static @NotNull AutoIncrement of(@NotNull SqliteTable table) {
        return new AutoIncrement() {
            @Override
            public @NotNull CompletableFuture<Integer> getAmount() {
                @NotNull CompletableFuture<Integer> future = new CompletableFuture<>();

                CompletableFuture.runAsync(() -> {
                    try {
                        @Nullable Connection connection = table.getDatabase().getConnection();
                        if (connection == null || connection.isClosed()) {
                            throw new IllegalStateException("The database's connection aren't connected");
                        }

                        try (@NotNull PreparedStatement statement = connection.prepareStatement("SELECT seq FROM sqlite_sequence WHERE name = '" + table.getId() + "';")) {
                            @NotNull ResultSet set = statement.executeQuery();

                            if (!set.next()) {
                                throw new IllegalStateException("the table '" + table.getId() + "' doesn't exists");
                            }

                            future.complete(set.getInt("auto_increment"));
                        }
                    } catch (@NotNull Throwable throwable) {
                        future.completeExceptionally(throwable);
                    }
                }, Main.getExecutor(getClass()));

                return future;
            }

            @Override
            public @NotNull CompletableFuture<Void> setAmount(int value) {
                throw new UnsupportedOperationException("cannot change sqlite table auto increment values");
            }
        };
    }

}
