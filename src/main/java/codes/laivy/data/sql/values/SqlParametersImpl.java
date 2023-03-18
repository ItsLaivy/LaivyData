package codes.laivy.data.sql.values;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;

/**
 * The native Parameters manager for the SQL databases of LaivyData.
 * This uses the JDBC's PreparedStatement.
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class SqlParametersImpl implements SqlParameters {

    private final @NotNull PreparedStatement statement;
    private final @Range(from = 0, to = Integer.MAX_VALUE) int index;

    public SqlParametersImpl(@NotNull PreparedStatement statement, @Range(from = 0, to = Integer.MAX_VALUE) int index) {
        this.statement = statement;
        this.index = index;
    }

    public @NotNull PreparedStatement getStatement() {
        return statement;
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getIndex() {
        return index;
    }

    @Override
    public void setString(@Nullable String string) {
        try {
            statement.setString(getIndex() + 1, string);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBoolean(boolean bool) {
        try {
            statement.setBoolean(getIndex() + 1, bool);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setInt(int i) {
        try {
            statement.setInt(getIndex() + 1, i);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setFloat(float f) {
        try {
            statement.setFloat(getIndex() + 1, f);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setDouble(double d) {
        try {
            statement.setDouble(getIndex() + 1, d);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setByte(byte b) {
        try {
            statement.setByte(getIndex() + 1, b);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBytes(byte[] bytes) {
        try {
            statement.setBytes(getIndex() + 1, bytes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBlob(@Nullable InputStream stream) {
        try {
            statement.setBlob(getIndex() + 1, stream);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLong(long l) {
        try {
            statement.setLong(getIndex() + 1, l);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setShort(short s) {
        try {
            statement.setShort(getIndex() + 1, s);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setObject(@Nullable Object o) {
        try {
            statement.setObject(getIndex() + 1, o);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNull(@NotNull SQLType type) {
        try {
            statement.setNull(getIndex() + 1, type.getVendorTypeNumber());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
