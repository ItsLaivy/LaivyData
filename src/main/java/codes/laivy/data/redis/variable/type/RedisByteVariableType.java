package codes.laivy.data.redis.variable.type;

import codes.laivy.data.redis.variable.RedisVariableType;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Base64;

/**
 * <p>
 *     The redis byte type are used to store byte arrays, you can save objects here too, it will convert the
 *     Serializable object into a byte array and store it on the database using the Base64 encoder/decoder.
 * </p>
 *<br>
 * <p>
 *     <b>Note:</b> The object that will be stored needs to be {@link Serializable} or {@link Byte} array. However, if you use this data type, it may not be compatible with other languages' LaivyData, and it could cause performance issues. Therefore, I strongly recommend that you create your own serialization system.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 */
public class RedisByteVariableType implements RedisVariableType {
    @Override
    public @Nullable String serialize(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof byte[]) {
            return Base64.getEncoder().encodeToString(serialize((byte[]) object));
        } else if (object instanceof Serializable) {
            return Base64.getEncoder().encodeToString(serialize((Serializable) object));
        } else {
            throw new IllegalArgumentException("To use the byte variable type, the object needs to be a byte[] or a Serializable!");
        }
    }

    @Override
    public @Nullable Object deserialize(@Nullable String value) {
        if (value == null) {
            return null;
        }

        return deserialize(Base64.getDecoder().decode(value));
    }

    protected byte[] serialize(@Nullable Serializable value) {
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

    protected @Nullable Serializable deserialize(byte[] value) {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(value);
            ObjectInputStream stream = new ObjectInputStream(b);
            return (Serializable) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean isCompatible(@Nullable Object object) {
        return object == null || object instanceof Serializable;
    }
}
