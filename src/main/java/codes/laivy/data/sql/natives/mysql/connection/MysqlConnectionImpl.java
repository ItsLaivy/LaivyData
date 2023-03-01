package codes.laivy.data.sql.natives.mysql.connection;

import codes.laivy.data.sql.natives.mysql.values.statement.MysqlResultStatement;
import codes.laivy.data.sql.natives.mysql.values.statement.MysqlResultStatementImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * The native MySQL Connection of LaivyData.
 * This uses the Java's native SQL connection.
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
@ApiStatus.Internal
public class MysqlConnectionImpl implements MysqlConnection {

    private final @NotNull Connection connection;

    public MysqlConnectionImpl(@NotNull Connection connection) {
        this.connection = connection;
    }

    public @NotNull Connection getConnection() {
        return connection;
    }

    @Override
    public @NotNull MysqlResultStatement createStatement(@NotNull String query) {
        return new MysqlResultStatementImpl(this, query);
    }
}
