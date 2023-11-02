package codes.laivy.data.mysql.table;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public interface AutoIncrement {

    default @NotNull CompletableFuture<Long> getAndIncrement(long increment) {
        @NotNull CompletableFuture<Long> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                long value = getAmount().join();
                setAmount(value + increment).join();

                future.complete(value);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @NotNull CompletableFuture<Long> getAmount();

    @NotNull CompletableFuture<Void> setAmount(long value);

    // Static initializers

    @ApiStatus.Internal
    static @NotNull AutoIncrement of(@NotNull MysqlTable table) {
        return new AutoIncrement() {
            @Override
            public @NotNull CompletableFuture<Long> getAmount() {
                @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();
                if (connection == null) {
                    throw new IllegalStateException("The database's authentication aren't connected");
                }

                @NotNull CompletableFuture<Long> future = new CompletableFuture<>();

                CompletableFuture.runAsync(() -> {
                    try {
                        try (@NotNull PreparedStatement statement = connection.prepareStatement("SHOW TABLE STATUS FROM `" + table.getDatabase().getId() + "` LIKE '" + table.getId() + "'")) {
                            @NotNull ResultSet set = statement.executeQuery();
                            set.next();

                            future.complete(set.getLong("auto_increment"));
                        }
                    } catch (@NotNull Throwable throwable) {
                        future.completeExceptionally(throwable);
                    }
                });

                return future;
            }

            @Override
            public @NotNull CompletableFuture<Void> setAmount(long value) {
                @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();
                if (connection == null) {
                    throw new IllegalStateException("The database's authentication aren't connected");
                }

                @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

                CompletableFuture.runAsync(() -> {
                    try {
                        try (@NotNull PreparedStatement statement = connection.prepareStatement("ALTER TABLE `" + table.getDatabase().getId() + "`.`" + table.getId() + "` AUTO_INCREMENT = " + value + ";")) {
                            statement.execute();
                        }

                        future.complete(null);
                    } catch (@NotNull Throwable throwable) {
                        future.completeExceptionally(throwable);
                    }
                });

                return future;
            }
        };
    }

}
