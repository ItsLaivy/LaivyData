package codes.laivy.data.sql.natives.mysql.connection;

import codes.laivy.data.sql.natives.mysql.values.statement.MysqlResultStatement;
import codes.laivy.data.sql.values.SqlConnection;
import org.jetbrains.annotations.NotNull;

public interface MysqlConnection extends SqlConnection {

    @Override
    @NotNull MysqlResultStatement createStatement(@NotNull String query);

}
