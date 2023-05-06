package codes.laivy.data.sql.mysql.natives;

import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

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
public abstract class MysqlConnectionNative implements MysqlConnection {

    private @NotNull Connection connection;

    public MysqlConnectionNative() {
        this.connection = connect();
    }

    /**
     * Gets the native java connection instance of this native MySQL Connection
     * @return the java connection
     *
     * @author Laivy
     * @since 1.0
     */
    public @NotNull Connection getConnection() {
        try {
            if (connection.isClosed()) {
                connection = connect();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull MysqlResultStatement createStatement(@NotNull String query) {
        return new MysqlResultStatementNative(this, query);
    }

}
