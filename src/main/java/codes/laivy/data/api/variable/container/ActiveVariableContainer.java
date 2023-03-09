package codes.laivy.data.api.variable.container;

import codes.laivy.data.api.variable.Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     This interface is responsible to hold the receptor's variable value.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 */
public interface ActiveVariableContainer extends VariableContainer {

    /**
     * The variable of this container, needs to be loaded.
     * @return the loaded variable
     */
    @NotNull Variable getVariable();

    /**
     * Sets the value of this variable container
     * @param value the new container value
     *
     * @author Laivy
     * @since 1.0
     */
    void set(@Nullable Object value);

}
