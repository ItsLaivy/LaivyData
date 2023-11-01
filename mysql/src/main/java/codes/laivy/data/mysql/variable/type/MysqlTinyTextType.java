package codes.laivy.data.mysql.variable.type;

import codes.laivy.data.mysql.variable.Parameter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public final class MysqlTinyTextType extends AbstractType<String> {
    public MysqlTinyTextType() {
        super("TINYTEXT");
    }

    @Override
    public void set(@NotNull Parameter parameter, @Nullable String value) {
        try {
            if (value != null) {
                parameter.setString(value);
            } else {
                parameter.setNull();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot set parameter value for '" + getClass().getSimpleName() + "' type", e);
        }
    }

    @Override
    public @Nullable String get(@Nullable Object object) {
        return String.valueOf(object);
    }

    @Override
    @Contract(pure = true)
    public boolean isNullSupported() {
        return true;
    }
}
