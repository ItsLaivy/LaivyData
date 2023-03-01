package codes.laivy.data.sql.mysql;

import codes.laivy.data.sql.SqlTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface MysqlTable extends SqlTable {
    @Override
    @NotNull MysqlDatabase getDatabase();

    @Override
    @Nullable MysqlVariable getLoadedVariable(@NotNull String id);
}
