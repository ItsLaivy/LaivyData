package codes.laivy.data.sql.sqlite;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.sqlite.variable.SqliteVariableType;
import org.jetbrains.annotations.NotNull;

public interface SqliteVariable extends SqlVariable {
    @Override
    @NotNull SqliteDatabase getDatabase();

    @Override
    @NotNull SqliteTable getTable();

    @Override
    @NotNull SqliteVariableType<?> getType();
}
