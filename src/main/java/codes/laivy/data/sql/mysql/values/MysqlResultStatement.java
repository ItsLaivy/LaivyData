package codes.laivy.data.sql.mysql.values;

import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.values.SqlResultStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Laivy
 * @since 1.0
 */
public interface MysqlResultStatement extends SqlResultStatement {
    @Override
    @NotNull MysqlConnection getConnection();

    @Override
    @Nullable MysqlResultData execute();
}
