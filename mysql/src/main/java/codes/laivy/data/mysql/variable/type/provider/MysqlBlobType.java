package codes.laivy.data.mysql.variable.type.provider;

import codes.laivy.data.mysql.variable.Parameter;
import codes.laivy.data.mysql.variable.type.AbstractType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Arrays;

public final class MysqlBlobType extends AbstractType<byte[]> {

    private final @NotNull Size size;

    public MysqlBlobType() {
        this(Size.BLOB);
    }
    public MysqlBlobType(@NotNull Size size) {
        super(size.name());
        this.size = size;
    }

    @Contract(pure = true)
    public @NotNull Size getSize() {
        return size;
    }

    @Override
    public void set(@NotNull Parameter parameter, byte @Nullable [] value) {
        if (value != null && value.length > getSize().getBytes()) {
            throw new IllegalArgumentException("The string overflow the type size '" + getSize().name() + "'");
        } else try {
            if (value != null) {
                parameter.setBytes(value);
            } else {
                parameter.setNull();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot set parameter value for '" + getClass().getSimpleName() + "' type", e);
        }
    }

    @Override
    public byte @Nullable [] get(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        byte @NotNull [] bytes;
        if (object instanceof byte[]) {
            bytes = (byte[]) object;
        } else try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            bytes = byteArrayOutputStream.toByteArray();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        return Arrays.copyOfRange(bytes, 0, (int) Math.min(bytes.length, getSize().getBytes()));
    }

    @Override
    public boolean isNullSupported() {
        return false;
    }

    public enum Size {
        TINYBLOB(255L),
        BLOB(65535L),
        MEDIUMBLOB(16777215L),
        LONGBLOB(4294967295L),
        ;

        private final long bytes;

        Size(long bytes) {
            this.bytes = bytes;
        }

        public long getBytes() {
            return bytes;
        }
    }
}
