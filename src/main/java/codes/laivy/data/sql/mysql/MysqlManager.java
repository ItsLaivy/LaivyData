package codes.laivy.data.sql.mysql;

import codes.laivy.data.sql.manager.SqlManager;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import org.jetbrains.annotations.NotNull;

public interface MysqlManager<R extends MysqlReceptor, V extends MysqlVariable, D extends MysqlDatabase, T extends MysqlTable> extends SqlManager<R, V, D, T> {
    @NotNull MysqlConnection getConnection();
}
