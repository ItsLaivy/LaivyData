package codes.laivy.data.redis.variable.container;

import codes.laivy.data.api.receptor.Receptor;
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
        set(object);
    }

    @Override
    @Contract(pure = true)
    public @NotNull RedisVariable getVariable() {
        return variable;
    }

    @Override
    public void set(@Nullable Object value) {
        if (!getVariable().getType().isCompatible(value)) {
            throw new IllegalStateException("This value isn't compatible with that variable type");
        }

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
