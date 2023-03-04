package codes.laivy.data.sql.sqlite;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.sqlite.connection.SqliteConnection;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public interface SqliteDatabase extends SqlDatabase {
    @Override
    @NotNull SqliteConnection getConnection();

    @Override
    @Pattern("^[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9]+)?$")
    @NotNull String getId();

    @Override
    @NotNull SqliteManager<SqliteReceptor, SqliteVariable, SqliteDatabase, SqliteTable> getManager();
}
