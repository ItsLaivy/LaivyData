package codes.laivy.data.api.variable.container;

import codes.laivy.data.api.receptor.Receptor;
import org.jetbrains.annotations.Nullable;

/**
 * The variable container
 *
 * @author Laivy
 * @since 1.0
 */
public interface VariableContainer {

    /**
     * Gets the receptor of this variable container, may be unloaded.
     * @return the receptor
     */
    @Nullable Receptor getReceptor();

    /**
     * Gets the value of this variable container, can be anything.
     * @return the variable container value
     */
    @Nullable Object get();

}
