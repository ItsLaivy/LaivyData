package codes.laivy.data.sql.sqlite;

import codes.laivy.data.sql.SqlReceptor;
import org.jetbrains.annotations.NotNull;

public interface SqliteReceptor extends SqlReceptor {
    @Override
    @NotNull SqliteDatabase getDatabase();

    @Override
    @NotNull SqliteTable getTable();
}
