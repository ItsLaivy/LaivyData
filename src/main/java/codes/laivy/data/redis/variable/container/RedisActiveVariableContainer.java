package codes.laivy.data.redis.variable.container;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.redis.RedisVariable;
import org.jetbrains.annotations.NotNull;

public interface RedisActiveVariableContainer extends ActiveVariableContainer {
    @Override
    @NotNull RedisVariable getVariable();


}
