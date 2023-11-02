package codes.laivy.data.mysql.variable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Parameter {

    @Range(from = 0, to = Integer.MAX_VALUE)
    private final int index;

    private Parameter(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        this.index = index;
    }

    /**
     * Gets the replacement index for this parameter manager
     * @return the replacement index
     */
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public final int getIndex() {
        return index;
    }

    public abstract void setString(@NotNull String string) throws SQLException;

    public abstract void setBoolean(boolean bool) throws SQLException;
    public abstract void setInt(int i) throws SQLException;
    public abstract void setFloat(float f) throws SQLException;
    public abstract void setDouble(double d) throws SQLException;
    public abstract void setByte(byte b) throws SQLException;
    public abstract void setBytes(byte[] bytes) throws SQLException;
    public abstract void setBlob(@NotNull InputStream stream) throws SQLException;
    public abstract void setLong(long l) throws SQLException;
    public abstract void setShort(short s) throws SQLException;
    public abstract void setObject(@NotNull Object o) throws SQLException;
    public abstract void setNull() throws SQLException, UnsupportedOperationException;

    // Static initializers

    public static @NotNull Parameter of(@NotNull PreparedStatement statement, boolean nullSupported, int index) {
        return new Parameter(index) {
            @Override
            public void setString(@NotNull String string) throws SQLException {
                statement.setString(getIndex() + 1, string);
            }

            @Override
            public void setBoolean(boolean bool) throws SQLException {
                statement.setBoolean(getIndex() + 1, bool);
            }

            @Override
            public void setInt(int i) throws SQLException {
                statement.setInt(getIndex() + 1, i);
            }

            @Override
            public void setFloat(float f) throws SQLException {
                statement.setFloat(getIndex() + 1, f);
            }

            @Override
            public void setDouble(double d) throws SQLException {
                statement.setDouble(getIndex() + 1, d);
            }

            @Override
            public void setByte(byte b) throws SQLException {
                statement.setByte(getIndex() + 1, b);
            }

            @Override
            public void setBytes(byte[] bytes) throws SQLException {
                statement.setBytes(getIndex() + 1, bytes);
            }

            @Override
            public void setBlob(@NotNull InputStream stream) throws SQLException {
                statement.setBlob(getIndex() + 1, stream);
            }

            @Override
            public void setLong(long l) throws SQLException {
                statement.setLong(getIndex() + 1, l);
            }

            @Override
            public void setShort(short s) throws SQLException {
                statement.setShort(getIndex() + 1, s);
            }

            @Override
            public void setObject(@Nullable Object o) throws SQLException {
                statement.setObject(getIndex() + 1, o);
            }

            @Override
            public void setNull() throws SQLException {
                if (nullSupported) {
                    statement.setNull(getIndex() + 1, 0);
                } else {
                    throw new UnsupportedOperationException("This parameter doesn't supports null operations");
                }
            }
        };
    }
}
