package codes.laivy.data.sql.mysql;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.variable.MysqlVariableType;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface MysqlVariable extends SqlVariable {
    @Override
    @NotNull MysqlDatabase getDatabase();

    @Override
    @NotNull MysqlTable getTable();

    @Override
    @NotNull MysqlVariableType<?> getType();
}
