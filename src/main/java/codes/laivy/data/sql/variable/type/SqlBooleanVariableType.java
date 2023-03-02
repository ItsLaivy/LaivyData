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
 *     The boolean type are used to store booleans.
 * </p>
 *<br>
 * <p>
 *     This variable type is compatible with others languages LaivyData.
 * </p>
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface SqlBooleanVariableType<V extends SqlVariable> extends SqlVariableType<V> {

    @Override
    @Contract(pure = true)
    @NotNull SqlDatabase getDatabase();

    @Override
    default void set(@Nullable Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata) {
        if (object == null) {
            parameters.setNull(getSqlType());
        } else if (object instanceof Boolean) {
            parameters.setBoolean((boolean) object);
        } else {
            throw new IllegalArgumentException("To use the byte variable type, the object needs to be a boolean!");
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

    default byte[] serialize(@Nullable Serializable value) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(b);
            stream.writeObject(value);
            return b.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    default @Nullable Serializable deserialize(byte[] value) {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(value);
            ObjectInputStream stream = new ObjectInputStream(b);
            return (Serializable) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }
}
