package codes.laivy.data.sql.natives.mysql.values.statement;

import codes.laivy.data.sql.natives.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.natives.mysql.values.data.MysqlResultData;
import codes.laivy.data.sql.values.SqlResultStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MysqlResultStatement extends SqlResultStatement {

    @Override
    @NotNull MysqlConnection getConnection();

    @Override
    @Nullable MysqlResultData execute();

}
