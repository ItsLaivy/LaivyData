package codes.laivy.data.api.variable.container;

import codes.laivy.data.api.variable.VariableType;
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
     * Gets the variable type of this container.
     * @return the variable type
     */
    @NotNull VariableType getType();

    /**
     * Sets the value of this variable container
     * @param value the new container value
     *
     * @author Laivy
     * @since 1.0
     */
    void set(@Nullable Object value);

}
