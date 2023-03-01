package codes.laivy.data.sql.natives.mysql.types;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.manager.SqlManager;
import codes.laivy.data.sql.natives.mysql.connection.MysqlConnection;
import org.jetbrains.annotations.NotNull;

public interface MysqlManager<R extends SqlReceptor, V extends SqlVariable, D extends SqlDatabase, T extends SqlTable> extends SqlManager<R, V, D, T> {

    @Override
    @NotNull MysqlConnection getConnection();

}
