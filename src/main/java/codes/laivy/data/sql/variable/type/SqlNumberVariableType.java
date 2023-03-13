package codes.laivy.data.sql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Stores the variable as number at the SQL database
 *
 * @author Laivy
 * @since 1.0
 */
public interface SqlNumberVariableType<V extends SqlVariable> extends SqlVariableType<V> {

    @Override
    default @UnknownNullability Number get(@UnknownNullability Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return (Number) object;
        }
        throw new IllegalArgumentException("This object doesn't seems to be a number instance!");
    }

    @Override
    default boolean isCompatible(@Nullable Object object) {
        return object == null || object instanceof Number;
    }

}
