package codes.laivy.data.mysql.variable.type;

import codes.laivy.data.mysql.variable.Parameter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class MysqlDoubleType extends AbstractType<Double> {

    public MysqlDoubleType() {
        super("DOUBLE");
    }

    @Override
    public void set(@NotNull Parameter parameter, @Nullable Double value) {
        try {
            if (value != null) {
                parameter.setDouble(value);
            } else {
                parameter.setNull();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot set parameter value for '" + getClass().getSimpleName() + "' type", e);
        }
    }

    @Override
    public @Nullable Double get(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        double value;
        if (object instanceof Double) {
            value = (Double) object;
        } else {
            value = Double.parseDouble(String.valueOf(object));
        }

        return value;
    }

    @Override
    public boolean isNullSupported() {
        return true;
    }
}
