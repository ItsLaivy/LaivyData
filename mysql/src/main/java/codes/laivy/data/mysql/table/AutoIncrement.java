package codes.laivy.data.mysql.table;

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
        @NotNull CompletableFuture<Integer> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                int value = getAmount().join();
                setAmount(value + increment).join();

                future.complete(value);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }

    @NotNull CompletableFuture<Integer> getAmount();

    @NotNull CompletableFuture<Void> setAmount(int value);

    // Static initializers

    @ApiStatus.Internal
    static @NotNull AutoIncrement of(@NotNull MysqlTable table) {
        return new AutoIncrement() {
            @Override
            public @NotNull CompletableFuture<Integer> getAmount() {
                @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();
                if (connection == null) {
                    throw new IllegalStateException("The database's authentication aren't connected");
                }

                @NotNull CompletableFuture<Integer> future = new CompletableFuture<>();

                CompletableFuture.runAsync(() -> {
                    try {
                        try (@NotNull PreparedStatement statement = connection.prepareStatement("SHOW TABLE STATUS FROM `" + table.getDatabase().getId() + "` LIKE '" + table.getId() + "'")) {
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
                @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();
                if (connection == null) {
                    throw new IllegalStateException("The database's authentication aren't connected");
                }

                @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

                CompletableFuture.runAsync(() -> {
                    try {
                        try (@NotNull PreparedStatement statement = connection.prepareStatement("ALTER TABLE `" + table.getDatabase().getId() + "`.`" + table.getId() + "` AUTO_INCREMENT = ?;")) {
                            statement.setInt(1, value);
                            statement.execute();
                        }

                        future.complete(null);
                    } catch (@NotNull Throwable throwable) {
                        future.completeExceptionally(throwable);
                    }
                }, Main.getExecutor(getClass()));

                return future;
            }
        };
    }

}
