package codes.laivy.data.mysql.variable.type;

import codes.laivy.data.mysql.variable.Parameter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class MysqlTextType extends AbstractType<String> {

    private final @NotNull Size size;

    public MysqlTextType() {
        this(Size.TEXT);
    }
    public MysqlTextType(@NotNull Size size) {
        super(size.name());
        this.size = size;
    }

    @Contract(pure = true)
    public @NotNull Size getSize() {
        return size;
    }

    @Override
    public void set(@NotNull Parameter parameter, @Nullable String value) {
        if (value != null && value.length() > getSize().getBytes()) {
            throw new IllegalArgumentException("The string overflow the type size '" + getSize().name() + "'");
        } else try {
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
        if (object == null) {
            return null;
        }

        @NotNull String string;
        if (object instanceof String) {
            string = (String) object;
        } else {
            string = String.valueOf(object);
        }

        return string.substring(0, (int) Math.min(string.length(), getSize().getBytes()));
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
