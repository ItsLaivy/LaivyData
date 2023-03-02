package codes.laivy.data.sql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * <p>
 *     The blob type are used to store byte arrays, you can save objects here too, it will convert the
 *     Serializable object into a byte array and store it on the database.
 * </p>
 *<br>
 * <p>
 *     <b>Note:</b> The object that will be stored needs to be {@link Serializable}. However, if you use this data type, it may not be compatible with other languages' LaivyData, and it could cause performance issues. Therefore, I strongly recommend that you create your own serialization system.
 * </p>
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface SqlByteVariableType<V extends SqlVariable> extends SqlVariableType<V> {

    default void set(@Nullable Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata) {
        if (object == null) {
            parameters.setNull(getSqlType());
        } else if (object instanceof byte[]) {
            parameters.setBytes((byte[]) object);
        } else if (object instanceof Serializable) {
            parameters.setBytes(serialize((Serializable) object));
        } else {
            throw new IllegalArgumentException("To use the byte variable type, the object needs to be a byte[] or a Serializable!");
        }
    }

    default @Nullable Object get(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof byte[]) {
            byte[] bytes = (byte[]) object;
            return deserialize(bytes);
        } else {
            throw new IllegalArgumentException("This object doesn't seems to be a byte object!");
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
