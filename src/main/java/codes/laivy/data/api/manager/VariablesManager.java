package codes.laivy.data.api.manager;

import codes.laivy.data.api.variable.Variable;
import org.jetbrains.annotations.NotNull;

/**
 * This controls the variables
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface VariablesManager<V extends Variable> extends Manager<V> {

    /**
     * Deletes the variable and all of its data.
     * @param variable the variable
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void delete(@NotNull V variable);

}
