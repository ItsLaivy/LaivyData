package codes.laivy.data.redis.variable.container;

import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.variable.RedisVariableType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Laivy
 * @since 1.0
 */
public class RedisActiveVariableContainerImpl implements RedisActiveVariableContainer {

    private final @NotNull RedisVariableType type;

    private final @Nullable RedisVariable variable;
    private final @Nullable RedisReceptor receptor;

    private @Nullable Object object;

    public RedisActiveVariableContainerImpl(@NotNull RedisVariable variable, @NotNull RedisReceptor receptor, @Nullable Object object) {
        this(variable, variable.getType(), receptor, object);
    }
    public RedisActiveVariableContainerImpl(@NotNull RedisVariableType type, @Nullable Object object) {
        this(null, type, null, object);
    }
    protected RedisActiveVariableContainerImpl(@Nullable RedisVariable variable, @NotNull RedisVariableType type, @Nullable RedisReceptor receptor, @Nullable Object object) {
        this.variable = variable;
        this.type = type;
        this.receptor = receptor;
        set(object);
    }

    @Override
    @Contract(pure = true)
    public @Nullable RedisVariable getVariable() {
        return variable;
    }

    @Override
    public @NotNull RedisVariableType getType() {
        return type;
    }

    @Override
    public void set(@Nullable Object value) {
        if (!getType().isCompatible(value)) {
            throw new IllegalStateException("This value isn't compatible with that variable type");
        }

        this.object = value;
    }

    @Override
    @Contract(pure = true)
    public @Nullable RedisReceptor getReceptor() {
        return receptor;
    }

    @Override
    public @Nullable Object get() {
        return object;
    }
}
