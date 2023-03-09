package codes.laivy.data.api.variable;

import org.jetbrains.annotations.Nullable;

/**
 * The VariableType controls, sets, gets, removes, and modifies the value of a variable. This is important.
 *
 * @author Laivy
 * @since 1.0
 */
public interface VariableType {
    /**
     * Checks if the object is compatible with that serialization type
     * @param object the object
     * @return true if this object can be used on this variable type, false if not.
     */
    boolean isCompatible(@Nullable Object object);
}
