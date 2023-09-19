package codes.laivy.data.sql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * <p>
 *     The {@link SqlTextVariableType} type converts the object into a string using the {@link Object#toString()} method, and returns this string at the {@link #get(Object)} method.
 * </p>
 *<br>
 * <p>
 *     This variable type is compatible with others languages LaivyData.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 */
public interface SqlTextVariableType<V extends SqlVariable> extends SqlVariableType<V> {

    @Override
    default void set(@Nullable Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata) {
        if (object != null && !Objects.equals(object, "null")) {
            parameters.setString(object.toString());
        } else {
            parameters.setNull(getSqlType());
        }
    }

    @Override
    default @Nullable String get(@Nullable Object object) {
        if (object == null || Objects.equals(object, "null")) {
            return null;
        }
        return object.toString();
    }

    @Override
    default boolean isCompatible(@Nullable Object object) {
        return true;
    }

}
