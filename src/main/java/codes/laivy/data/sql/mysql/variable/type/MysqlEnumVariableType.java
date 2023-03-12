package codes.laivy.data.sql.mysql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.variable.MysqlVariableType;
import codes.laivy.data.sql.variable.type.SqlEnumVariableType;
import org.jetbrains.annotations.NotNull;

import java.sql.JDBCType;
import java.sql.SQLType;

public class MysqlEnumVariableType<E extends Enum<?>> implements SqlEnumVariableType<MysqlVariable, E>, MysqlVariableType<MysqlVariable> {

    private final @NotNull Class<E> enumClass;
    protected @NotNull SQLType type;

    public MysqlEnumVariableType(@NotNull Class<E> enumClass) {
        this.enumClass = enumClass;
        type = new SQLType() {
            @Override
            public String getName() {
                StringBuilder types = new StringBuilder();
                int row = 0;
                for (Enum<?> e : getEnum().getEnumConstants()) {
                    if (row != 0) types.append(",");
                    types.append("'").append(e.name()).append("'");
                    row++;
                }

                return "ENUM(" + types + ")";
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
    public @NotNull SQLType getSqlType() {
        return type;
    }

    @Override
    public void configure(@NotNull SqlVariable variable) {
        variable.getDatabase().getManager().getVariablesManager().setType(variable, getSqlType());
    }

    @Override
    public @NotNull Class<E> getEnum() {
        return enumClass;
    }
}
