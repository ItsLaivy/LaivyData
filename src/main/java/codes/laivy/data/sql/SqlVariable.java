package codes.laivy.data.sql;

import codes.laivy.data.api.table.Tableable;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.sql.variable.type.SqlVariableConfiguration;
import codes.laivy.data.sql.variable.type.SqlVariableType;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SqlVariable extends Variable, Tableable {
    @Nullable SqlVariableConfiguration getConfiguration();

    @Override
    @Pattern(".*")
    @NotNull String getId();

    @Override
    void setId(@NotNull @Pattern(".*") @Subst("variable_name") String id);

    @Override
    @Contract(pure = true)
    @NotNull SqlDatabase getDatabase();
    @Override
    @Contract(pure = true)
    @NotNull SqlTable getTable();

    @Override
    @Contract(pure = true)
    @NotNull SqlVariableType getType();
}
