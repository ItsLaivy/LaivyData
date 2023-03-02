package codes.laivy.data.sql.sqlite;

import codes.laivy.data.sql.manager.SqlManager;
import codes.laivy.data.sql.sqlite.connection.SqliteConnection;
import org.jetbrains.annotations.NotNull;

public interface SqliteManager extends SqlManager<SqliteReceptor, SqliteVariable, SqliteDatabase, SqliteTable> {
    @Override
    @NotNull SqliteConnection getConnection();
}
