package codes.laivy.data.mysql.variable.type.provider;

import codes.laivy.data.mysql.variable.Parameter;
import codes.laivy.data.mysql.variable.type.AbstractType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public final class MysqlLongType extends AbstractType<Long> {

    public MysqlLongType() {
        super("BIGINT(19)");
    }

    @Override
    public void set(@NotNull Parameter parameter, @Nullable Long value) {
        try {
            if (value != null) {
                parameter.setLong(value);
            } else {
                parameter.setNull();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot set parameter value for '" + getClass().getSimpleName() + "' type", e);
        }
    }

    @Override
    public @Nullable Long get(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        long value;
        if (object instanceof Long) {
            value = (Long) object;
        } else {
            value = Long.parseLong(String.valueOf(object));
        }

        return value;
    }

    @Override
    public boolean isNullSupported() {
        return true;
    }
}
