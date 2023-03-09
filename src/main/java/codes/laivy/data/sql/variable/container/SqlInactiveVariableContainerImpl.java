package codes.laivy.data.sql.variable.container;

import codes.laivy.data.sql.SqlReceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqlInactiveVariableContainerImpl implements SqlInactiveVariableContainer {

    private final @NotNull String variable;
    private final @NotNull SqlReceptor receptor;
    private final @Nullable Object object;

    public SqlInactiveVariableContainerImpl(@NotNull String variable, @NotNull SqlReceptor receptor, @Nullable Object object) {
        this.variable = variable;
        this.receptor = receptor;
        this.object = object;
    }

    @Override
    public @NotNull String getVariable() {
        return variable;
    }

    @Override
    public @Nullable Object get() {
        return object;
    }

    @Override
    public @NotNull SqlReceptor getReceptor() {
        return receptor;
    }
}
