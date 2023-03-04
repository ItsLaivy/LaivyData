package codes.laivy.data.api.variable.container;

import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     This class can be used to store the value of an inactive/unloaded variable, and convert into a {@link ActiveVariableContainer} again if the variable loads.
 * </p>
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface InactiveVariableContainer extends VariableContainer {

    /**
     * As it's impossible to get the configurations/type of unloaded variable, this variable doesn't contain an instance, is saved as a String.
     * @return The unloaded variable name
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull String getVariable();

}
