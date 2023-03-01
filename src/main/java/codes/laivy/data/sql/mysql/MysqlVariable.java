package codes.laivy.data.sql.mysql;

import codes.laivy.data.sql.SqlVariable;
import org.jetbrains.annotations.NotNull;

public interface MysqlVariable extends SqlVariable {
    @Override
    @NotNull MysqlDatabase getDatabase();

    @Override
    @NotNull MysqlTable getTable();
}
