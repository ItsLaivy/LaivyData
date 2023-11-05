package codes.laivy.data.mysql.variable.type;

import codes.laivy.data.Main;
import codes.laivy.data.mysql.variable.MysqlVariable;
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
    public final @NotNull CompletableFuture<Boolean> configure(@NotNull MysqlVariable<T> variable) {
        @Nullable Connection connection = variable.getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The variable's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
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
