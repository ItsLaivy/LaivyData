package codes.laivy.data.redis;

import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.redis.variable.RedisVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisVariable extends Variable {

    @Override
    @NotNull RedisDatabase getDatabase();

    /**
     * The redis table is the variable's table, could be null.
     * @return the table, null if it doesn't have one.
     */
    @Nullable RedisTable getTable();

    @Override
    @NotNull RedisVariableType getType();

    @Override
    @NotNull String getId();

    @Override
    void setId(@NotNull String id);
}
