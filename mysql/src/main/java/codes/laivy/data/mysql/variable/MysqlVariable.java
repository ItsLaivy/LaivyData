package codes.laivy.data.mysql.variable;

import codes.laivy.data.mysql.MysqlDatabase;
import codes.laivy.data.mysql.MysqlTable;
import codes.laivy.data.mysql.utils.SqlUtils;
import codes.laivy.data.variable.Variable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public abstract class MysqlVariable<T> extends Variable<T> {

    private final @NotNull MysqlTable table;
    private final @NotNull Type<T> type;

    public MysqlVariable(@NotNull String id, @NotNull MysqlTable table, @NotNull Type<T> type) {
        super(id);

        this.table = table;
        this.type = type;

        if (!id.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This variable id '" + id + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
        }
    }

    @Contract(pure = true)
    public final @NotNull MysqlDatabase getDatabase() {
        return getTable().getDatabase();
    }

    @Contract(pure = true)
    public final @NotNull MysqlTable getTable() {
        return table;
    }

    @Contract(pure = true)
    public final @NotNull Type<T> getType() {
        return type;
    }

    @Override
    protected @NotNull CompletableFuture<Void> load() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                getType().configure(this).join();
                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @Override
    protected @NotNull CompletableFuture<Void> unload() {
        return CompletableFuture.runAsync(() -> {

        });
    }

    @Override
    public @NotNull CompletableFuture<Boolean> delete() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (PreparedStatement statement = getDatabase().getAuthentication().getConnection().prepareStatement("ALTER TABLE `" + getDatabase().getId() + "`.`" + getTable().getName() + "` DROP COLUMN `" + getId() + "`")) {
                if (isLoaded()) {
                    unload().join();
                }

                statement.execute();

                future.complete(true);
            } catch (@NotNull Throwable throwable) {
                if (SqlUtils.getErrorCode(throwable) == 1091) {
                    future.complete(false);
                } else {
                    future.completeExceptionally(throwable);
                }
            }
        });

        return future;
    }
}
