package codes.laivy.data.redis.variable.container;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.redis.RedisVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedisActiveVariableContainerImpl implements RedisActiveVariableContainer {

    private final @NotNull RedisVariable variable;
    private final @NotNull Receptor receptor;
    private @Nullable Object object;

    public RedisActiveVariableContainerImpl(@NotNull RedisVariable variable, @NotNull Receptor receptor, @Nullable Object object) {
        this.variable = variable;
        this.receptor = receptor;
        this.object = object;
    }

    @Override
    @Contract(pure = true)
    public @NotNull RedisVariable getVariable() {
        return variable;
    }

    @Override
    public void set(@Nullable Object value) {
        this.object = value;
    }

    @Override
    @Contract(pure = true)
    public @NotNull Receptor getReceptor() {
        return receptor;
    }

    @Override
    public @Nullable Object get() {
        return object;
    }
}
