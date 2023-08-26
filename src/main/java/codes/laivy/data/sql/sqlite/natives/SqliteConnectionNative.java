package codes.laivy.data.sql.sqlite.natives;

import codes.laivy.data.sql.sqlite.connection.SqliteConnection;
import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * <p>
 *     The native SQLite Connection of LaivyData.
 *     This uses the Java's native SQL connection.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class SqliteConnectionNative implements SqliteConnection {

    private final @NotNull Connection connection;

    public SqliteConnectionNative(@NotNull Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets the native java connection instance of this native SQLite Connection
     * @return the java connection
     *
     * @author Laivy
     * @since 1.0
     */
    @Override
    public @NotNull Connection getConnection() {
        return connection;
    }

    @Override
    public @NotNull SqliteResultStatement createStatement(@NotNull String query) {
        return new SqliteResultStatementNative(this, query);
    }
}
