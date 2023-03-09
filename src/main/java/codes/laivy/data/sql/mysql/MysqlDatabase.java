package codes.laivy.data.sql.mysql;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface MysqlDatabase extends SqlDatabase {
    @Override
    @NotNull MysqlConnection getConnection();

    @Override
    @Pattern("^.$")
    @NotNull String getId();

    @Override
    @NotNull MysqlManager<MysqlReceptor, MysqlVariable, MysqlDatabase, MysqlTable> getManager();
}
