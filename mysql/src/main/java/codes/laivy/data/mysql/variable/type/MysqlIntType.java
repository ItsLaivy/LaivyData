package codes.laivy.data.mysql.variable.type;

import codes.laivy.data.mysql.variable.Parameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class MysqlIntType extends AbstractType<Integer> {

    public MysqlIntType() {
        super("INT");
    }

    @Override
    public void set(@NotNull Parameter parameter, @Nullable Integer value) {
        try {
            if (value != null) {
                parameter.setInt(value);
            } else {
                parameter.setNull();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot set parameter value for '" + getClass().getSimpleName() + "' type", e);
        }
    }

    @Override
    public @Nullable Integer get(@Nullable Object object) {
        if (object == null) {
            return null;
        }

        int value;
        if (object instanceof Integer) {
            value = (Integer) object;
        } else {
            value = Integer.parseInt(String.valueOf(object));
        }

        return value;
    }

    @Override
    public boolean isNullSupported() {
        return true;
    }
}
