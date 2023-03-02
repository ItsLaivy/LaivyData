package codes.laivy.data.sql.mysql.variable;

import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.variable.SqlVariableType;
import org.jetbrains.annotations.NotNull;

public interface MysqlVariableType<V extends MysqlVariable> extends SqlVariableType<V> {
    @Override
    @NotNull MysqlDatabase getDatabase();
}
