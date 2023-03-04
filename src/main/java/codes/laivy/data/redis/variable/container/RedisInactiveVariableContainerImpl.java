package codes.laivy.data.redis.variable.container;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedisInactiveVariableContainerImpl implements InactiveVariableContainer {

    private final @NotNull String variable;
    private final @NotNull Receptor receptor;
    private final @Nullable Object object;

    public RedisInactiveVariableContainerImpl(@NotNull String variable, @NotNull Receptor receptor, @Nullable Object object) {
        this.variable = variable;
        this.receptor = receptor;
        this.object = object;
    }

    @Override
    @Contract(pure = true)
    public @NotNull String getVariable() {
        return variable;
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
