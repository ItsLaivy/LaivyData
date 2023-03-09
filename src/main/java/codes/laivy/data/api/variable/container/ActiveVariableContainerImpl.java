package codes.laivy.data.api.variable.container;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.variable.Variable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActiveVariableContainerImpl implements ActiveVariableContainer {

    private final @NotNull Variable variable;
    private final @NotNull Receptor receptor;
    private @Nullable Object object;

    public ActiveVariableContainerImpl(@NotNull Variable variable, @NotNull Receptor receptor, @Nullable Object object) {
        this.variable = variable;
        this.receptor = receptor;
        set(object);
    }

    @Override
    @Contract(pure = true)
    public @NotNull Variable getVariable() {
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
