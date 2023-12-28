package codes.laivy.data.mysql.variable.type.provider;

import codes.laivy.data.mysql.variable.Parameter;
import codes.laivy.data.mysql.variable.type.AbstractType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class MysqlFloatType extends AbstractType<Float> {

    public MysqlFloatType() {
        super("FLOAT");
    }

    @Override
    public void set(@NotNull Parameter parameter, @Nullable Float value) {
        try {
            if (value != null) {
                parameter.setFloat(value);
            } else {
                parameter.setNull();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot set parameter value for '" + getClass().getSimpleName() + "' type", e);
        }
    }

    @Override
    public @Nullable Float get(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        float value;
        if (object instanceof Float) {
            value = (Float) object;
        } else {
            value = Float.parseFloat(String.valueOf(object));
        }

        return value;
    }

    @Override
    public boolean isNullSupported() {
        return true;
    }
}
