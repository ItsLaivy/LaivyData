package codes.laivy.data.sql.values;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.InputStream;
import java.sql.SQLType;

/**
 * The SQL parameters are used to easily bind the parameters of an index
 * 
 * @author Laivy
 * @since 1.0
 */
public interface SqlParameters {

    /**
     * Gets the replacement index for this parameter manager
     * @return the replacement index
     */
    @Range(from = 0, to = Integer.MAX_VALUE) int getIndex();

    void setString(@Nullable String string);
    void setBoolean(boolean bool);
    void setInt(int i);
    void setFloat(float f);
    void setDouble(double d);
    void setByte(byte b);
    void setBytes(byte[] bytes);
    void setBlob(@Nullable InputStream stream);
    void setLong(long l);
    void setShort(short s);
    void setObject(@Nullable Object o);
    void setNull(@NotNull SQLType type);

}
