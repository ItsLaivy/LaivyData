package codes.laivy.data.sql.sqlite.variable;

import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;

public interface SqliteVariableType<V extends SqliteVariable> extends SqlVariableType<V> {
    @Override
    @NotNull SqliteDatabase getDatabase();
}
