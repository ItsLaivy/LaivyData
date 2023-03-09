package codes.laivy.data.redis.variable.container;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.variable.RedisVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisActiveVariableContainer extends ActiveVariableContainer {

    /**
     * The redis container's variable.
     * @return The redis container's variable or null if not applicable
     */
    @Nullable RedisVariable getVariable();

    @Override
    @NotNull RedisVariableType getType();

    @Override
    @Nullable RedisReceptor getReceptor();
}
