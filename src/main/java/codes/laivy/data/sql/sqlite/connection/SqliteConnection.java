package codes.laivy.data.sql.sqlite.connection;

import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import codes.laivy.data.sql.values.SqlConnection;
import org.jetbrains.annotations.NotNull;

public interface SqliteConnection extends SqlConnection {
    @Override
    @NotNull SqliteResultStatement createStatement(@NotNull String query);
}
