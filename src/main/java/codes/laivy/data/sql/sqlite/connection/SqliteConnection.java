package codes.laivy.data.sql.sqlite.connection;

import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import codes.laivy.data.sql.values.SqlConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqliteConnection extends SqlConnection {
    @Override
    @NotNull SqliteResultStatement createStatement(@NotNull String query);

    @NotNull Connection getConnection();

}
