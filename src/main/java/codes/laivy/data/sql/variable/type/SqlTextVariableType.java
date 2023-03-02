package codes.laivy.data.sql.variable.type;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * <p>
 *     The {@link SqlTextVariableType} type converts the object into a string using the {@link Object#toString()} method, and returns this string at the {@link #get(Object)} method.
 * </p>
 *<br>
 * <p>
 *     This variable type is compatible with others languages LaivyData.
 * </p>
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface SqlTextVariableType<V extends SqlVariable> extends SqlVariableType<V> {

    @Override
    @Contract(pure = true)
    @NotNull SqlDatabase getDatabase();

    @Override
    default void set(@Nullable Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata) {
        if (object != null) {
            parameters.setString(object.toString());
        } else {
            parameters.setNull(getSqlType());
        }
    }

    @Override
    default @Nullable String get(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }
}
