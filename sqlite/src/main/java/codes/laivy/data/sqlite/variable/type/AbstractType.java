package codes.laivy.data.sqlite.variable.type;

import codes.laivy.data.Main;
import codes.laivy.data.sqlite.variable.SqliteVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractType<T> implements Type<T> {

    private final @NotNull String sqlName;

    public AbstractType(@NotNull String sqlName) {
        this.sqlName = sqlName;
    }

    @Contract(pure = true)
    public final @NotNull String getSqlName() {
        return sqlName;
    }

    @Override
    public final @NotNull CompletableFuture<Boolean> configure(@NotNull SqliteVariable<T> variable) {
        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @Nullable Connection connection = variable.getDatabase().getConnection();
                if (connection == null || connection.isClosed()) {
                    throw new IllegalStateException("The database's authentication aren't connected");
                }

                boolean exists = variable.exists().join();

                if (!exists) {
                    try (@NotNull PreparedStatement statement = connection.prepareStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` ADD COLUMN `" + variable.getId() + "` " + getSqlName() + (variable.isNullable() ? "" : " NOT NULL") + ";")) {
                        statement.execute();
                    }
                } else try (@NotNull PreparedStatement statement = connection.prepareStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` MODIFY COLUMN `" + variable.getId() + "` " + getSqlName() + ";")) {
                    statement.execute();
                }

                future.complete(!exists);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

        return future;
    }
}
