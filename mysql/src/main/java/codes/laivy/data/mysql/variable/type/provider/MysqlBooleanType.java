package codes.laivy.data.mysql.variable.type.provider;

import codes.laivy.data.mysql.variable.Parameter;
import codes.laivy.data.mysql.variable.type.AbstractType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public final class MysqlBooleanType extends AbstractType<Boolean> {
    public MysqlBooleanType() {
        super("BOOL");
    }

    @Override
    public void set(@NotNull Parameter parameter, @Nullable Boolean value) {
        try {
            if (value != null) {
                parameter.setBoolean(value);
            } else {
                parameter.setNull();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot set parameter value for '" + getClass().getSimpleName() + "' type", e);
        }
    }

    @Override
    public @Nullable Boolean get(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof Boolean) {
            return (Boolean) object;
        }

        try {
            int integer = Integer.parseInt(String.valueOf(object));
            return integer != 0;
        } catch (NumberFormatException ignore) {
            return Boolean.valueOf(object.toString());
        }
    }

    @Override
    public boolean isNullSupported() {
        return true;
    }

    public enum Size {
        TINYTEXT(255L),
        TEXT(65535L),
        MEDIUMTEXT(16777215L),
        LONGTEXT(4294967295L),
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
