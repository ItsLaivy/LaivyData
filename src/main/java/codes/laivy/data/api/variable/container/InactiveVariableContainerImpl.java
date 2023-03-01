package codes.laivy.data.api.variable.container;

import codes.laivy.data.api.receptor.Receptor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InactiveVariableContainerImpl implements InactiveVariableContainer {

    private final @NotNull String variable;
    private final @NotNull Receptor receptor;
    private final @Nullable Object object;

    public InactiveVariableContainerImpl(@NotNull String variable, @NotNull Receptor receptor, @Nullable Object object) {
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
