package codes.laivy.data.sql.variable.container;

import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqlActiveVariableContainerImpl implements SqlActiveVariableContainer {

    private final @NotNull SqlVariableType<?> type;

    private final @Nullable SqlVariable variable;
    private final @Nullable SqlReceptor receptor;

    private @Nullable Object object;

    public SqlActiveVariableContainerImpl(@NotNull SqlVariable variable, @NotNull SqlReceptor receptor, @Nullable Object object) {
        this(variable, variable.getType(), receptor, object);
    }
    public SqlActiveVariableContainerImpl(@NotNull SqlVariableType<?> type, @Nullable Object object) {
        this(null, type, null, object);
    }
    protected SqlActiveVariableContainerImpl(@Nullable SqlVariable variable, @NotNull SqlVariableType<?> type, @Nullable SqlReceptor receptor, @Nullable Object object) {
        this.variable = variable;
        this.type = type;
        this.receptor = receptor;
        set(object);
    }

    @Override
    @Contract(pure = true)
    public @Nullable SqlVariable getVariable() {
        return variable;
    }

    @Override
    public @NotNull SqlVariableType<?> getType() {
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
    public @Nullable SqlReceptor getReceptor() {
        return receptor;
    }

    @Override
    public @Nullable Object get() {
        return object;
    }
}
