package codes.laivy.data.sql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Stores the variable as boolean at the SQL database
 *
 * @author Laivy
 * @since 1.0
 */
public interface SqlEnumVariableType<V extends SqlVariable, E extends Enum<?>> extends SqlVariableType<V> {

    @Override
    default void set(@UnknownNullability Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata) {
        if (isCompatible(object)) {
            if (object == null) {
                parameters.setNull(getSqlType());
            } else {
                //noinspection unchecked
                E enumObj = (E) object;
                parameters.setString(enumObj.name());
            }
        } else {
            if (!allowNull() && object == null) {
                throw new IllegalArgumentException("This object is null, and this enum variable type doesn't supports null values");
            } else {
                throw new IllegalArgumentException("This object doesn't seems to be compatible with this enum type");
            }
        }
    }

    @Override
    default @UnknownNullability E get(@UnknownNullability Object object) {
        if (object == null) {
            if (allowNull()) {
                return null;
            } else {
                throw new IllegalArgumentException("This object is null, and this enum variable type doesn't supports null values");
            }
        }

        for (E c : getEnum().getEnumConstants()) {
            if (c.equals(object) || c.name().equals(object.toString())) {
                return c;
            }
        }

        throw new IllegalArgumentException("This object doesn't seems to be a '" + getEnum().getName() + "' enum constant!");
    }

    @NotNull Class<E> getEnum();

    @Override
    default boolean isCompatible(@Nullable Object object) {
        if (!allowNull() && object == null) {
            System.out.println("False 1");
            return false;
        } else if (object != null && getEnum().isAssignableFrom(object.getClass())) {
            return true;
        } else if (object instanceof String) {
            for (E e : getEnum().getEnumConstants()) {
                if (e.name().equals(object.toString())) {
                    return true;
                }
            }
            System.out.println("False 2");
            return false;
        }
        System.out.println("False 3");
        return false;
    }

    /**
     * This represents if the enum column will allow null values or not
     * @return true if you can store a null value, false otherwise
     */
    default boolean allowNull() {
        return false;
    }
}
