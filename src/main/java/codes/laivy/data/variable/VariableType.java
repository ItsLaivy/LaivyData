package codes.laivy.data.variable;

import org.jetbrains.annotations.Nullable;

/**
 * Controls, sets, gets, removes, and modifies the value of a variable.
 * This interface defines methods to handle variable-related operations.
 *
 * @param <T> The type of the variable
 * @since 1.0
 */
public abstract class VariableType<T> {
    /**
     * Checks if the object is compatible with this variable type.
     *
     * @param object The object to check for compatibility
     * @return True if the object is compatible with this variable type, false otherwise
     * @since 1.0
     */
    public abstract boolean isCompatible(@Nullable Object object);

    /**
     * Parses the given object into the variable type.
     *
     * @param object The object to parse
     * @return The parsed object as the specified variable type, or null if parsing is not possible
     * @since 2.0
     */
    public abstract @Nullable T parse(@Nullable Object object);

    /**
     * Serializes the specified object into a suitable representation.
     *
     * @param object The object to serialize
     * @return The serialized object as an appropriate representation or null if the object is null
     * @since 2.0
     */
    public abstract @Nullable Object serialize(@Nullable T object);
}
