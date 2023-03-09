package codes.laivy.data.sql.variable.container;

import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.sql.SqlReceptor;
import org.jetbrains.annotations.NotNull;

public interface SqlInactiveVariableContainer extends InactiveVariableContainer {
    @Override
    @NotNull SqlReceptor getReceptor();
}
