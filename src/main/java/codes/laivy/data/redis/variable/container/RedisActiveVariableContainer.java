package codes.laivy.data.redis.variable.container;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.redis.RedisVariable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisActiveVariableContainer extends ActiveVariableContainer {
    @Override
    @NotNull RedisVariable getVariable();
}
