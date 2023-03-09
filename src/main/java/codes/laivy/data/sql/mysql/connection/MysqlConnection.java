package codes.laivy.data.sql.mysql.connection;

import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import codes.laivy.data.sql.values.SqlConnection;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface MysqlConnection extends SqlConnection {

    @Override
    @NotNull MysqlResultStatement createStatement(@NotNull String query);

}
