package codes.laivy.data.sql.mysql.natives;

import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * <p>
 *     The native MySQL Connection of LaivyData.
 *     This uses the Java's native SQL connection.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
@ApiStatus.Internal
public class MysqlConnectionNative implements MysqlConnection {

    private final @NotNull Connection connection;

    public MysqlConnectionNative(@NotNull Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets the native java connection instance of this native MySQL Connection
     * @return the java connection
     *
     * @author Laivy
     * @since 1.0
     */
    public @NotNull Connection getConnection() {
        return connection;
    }

    @Override
    public @NotNull MysqlResultStatement createStatement(@NotNull String query) {
        return new MysqlResultStatementNative(this, query);
    }
}
