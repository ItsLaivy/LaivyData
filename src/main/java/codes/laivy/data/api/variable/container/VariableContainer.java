package codes.laivy.data.api.variable.container;

import codes.laivy.data.api.receptor.Receptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface VariableContainer {

    /**
     * Gets the receptor of this variable container, may be unloaded.
     * @return the receptor
     */
    @NotNull Receptor getReceptor();

    /**
     * Gets the value of this variable container, can be anything.
     * @return the variable container value
     */
    @Nullable Object get();

}
