package codes.laivy.data.sql;

import codes.laivy.data.api.table.Table;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqlTable extends Table {

    /**
     * This set <b>MUST</b> contains only loaded receptors
     * @return all loaded receptors at this table
     *
     * @author Laivy
     * @since 1.0
     */
    @NotNull Set<SqlReceptor> getLoadedReceptors();

    /**
     * This set <b>MUST</b> contains only loaded variables
     * @return all loaded variables at this table
     *
     * @author Laivy
     * @since 1.0
     */
    @NotNull Set<SqlVariable> getLoadedVariables();

    /**
     * Gets a loaded variable at this table
     * @param id the variable id
     * @return a loaded variable or null if a variable with that id doesn't exist at this table
     */
    default @Nullable SqlVariable getLoadedVariable(@NotNull String id) {
        for (SqlVariable variable : getLoadedVariables()) {
            if (variable.getId().equals(id)) {
                return variable;
            }
        }
        return null;
    }

    @Override
    @Contract(pure = true)
    @NotNull SqlDatabase getDatabase();

    @Override
    @NotNull String getId();

    @Override
    void setId(@NotNull String id);
}
