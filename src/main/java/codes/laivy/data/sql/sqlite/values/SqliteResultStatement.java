package codes.laivy.data.sql.sqlite.values;

import codes.laivy.data.sql.sqlite.connection.SqliteConnection;
import codes.laivy.data.sql.values.SqlResultStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SqliteResultStatement extends SqlResultStatement {
    @Override
    @NotNull SqliteConnection getConnection();

    @Override
    @Nullable SqliteResultData execute();
}
