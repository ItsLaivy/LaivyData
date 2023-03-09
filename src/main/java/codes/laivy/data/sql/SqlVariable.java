package codes.laivy.data.sql;

import codes.laivy.data.api.table.Tableable;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.sql.variable.SqlVariableConfiguration;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqlVariable extends Variable, Tableable {
    @Nullable SqlVariableConfiguration getConfiguration();

    @Override
    @Pattern(".*")
    @NotNull String getId();

    @Override
    void setId(@NotNull @Pattern(".*") @Subst("variable_id") String id);

    @Override
    @Contract(pure = true)
    @NotNull SqlDatabase getDatabase();
    @Override
    @Contract(pure = true)
    @NotNull SqlTable getTable();

    @Override
    @Contract(pure = true)
    @NotNull SqlVariableType<?> getType();
}
