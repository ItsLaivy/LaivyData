package codes.laivy.data.sql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SqlBooleanVariableType<V extends SqlVariable> extends SqlVariableType<V> {

    @Override
    default void set(@Nullable Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata) {
        if (object == null) {
            parameters.setNull(getSqlType());
        } else if (object instanceof Boolean) {
            parameters.setBoolean((boolean) object);
        } else {
            throw new IllegalArgumentException("To use the boolean variable type, the object needs to be a boolean!");
        }
    }

    @Override
    default @Nullable Boolean get(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            throw new IllegalArgumentException("This object doesn't seems to be a boolean object!");
        }
    }

    @Override
    default boolean isCompatible(@Nullable Object object) {
        return object == null || object instanceof Boolean;
    }
}
