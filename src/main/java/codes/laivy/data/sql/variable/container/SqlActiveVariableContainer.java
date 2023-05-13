package codes.laivy.data.sql.variable.container;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SqlActiveVariableContainer extends ActiveVariableContainer {

    /**
     * Gets the variable of this container
     * @return the variable or null if not applicable
     */
    @Nullable SqlVariable getVariable();

    @Override
    @NotNull SqlVariableType<? extends SqlVariable> getType();

    @Override
    @Nullable SqlReceptor getReceptor();
}
