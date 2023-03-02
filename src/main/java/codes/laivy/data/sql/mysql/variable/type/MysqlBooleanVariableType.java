package codes.laivy.data.sql.mysql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.variable.MysqlVariableType;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import codes.laivy.data.sql.variable.type.SqlBooleanVariableType;
import org.jetbrains.annotations.NotNull;

import java.sql.JDBCType;
import java.sql.SQLType;

public class MysqlBooleanVariableType implements SqlBooleanVariableType<MysqlVariable>, MysqlVariableType<MysqlVariable> {

    private final @NotNull MysqlDatabase database;
    protected @NotNull SQLType type;

    public MysqlBooleanVariableType(@NotNull MysqlDatabase database) {
        this.database = database;

        type = new SQLType() {
            @Override
            public String getName() {
                return "BOOLEAN";
            }

            @Override
            public String getVendor() {
                return "codes.laivy.data.sql.natives.mysql.variable.type";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return JDBCType.BOOLEAN.getVendorTypeNumber();
            }
        };
    }

    @Override
    public @NotNull MysqlDatabase getDatabase() {
        return database;
    }

    @Override
    public @NotNull SQLType getSqlType() {
        return type;
    }

    @Override
    public void configure(@NotNull SqlVariable variable) {
        variable.getDatabase().getManager().getVariablesManager().setType(variable, getSqlType());
    }
}
