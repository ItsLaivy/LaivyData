package codes.laivy.data.sql.mysql;

import codes.laivy.data.sql.manager.SqlManager;
import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface MysqlManager<R extends MysqlReceptor, V extends MysqlVariable, D extends MysqlDatabase, T extends MysqlTable> extends SqlManager<R, V, D, T> {
    @NotNull MysqlConnection getConnection();
}
