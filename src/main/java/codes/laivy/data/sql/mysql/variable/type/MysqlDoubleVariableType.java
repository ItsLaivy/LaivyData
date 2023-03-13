package codes.laivy.data.sql.mysql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.variable.MysqlVariableType;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import codes.laivy.data.sql.variable.type.SqlNumberVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.JDBCType;
import java.sql.SQLType;

public class MysqlDoubleVariableType implements SqlNumberVariableType<MysqlVariable>, MysqlVariableType<MysqlVariable> {
    @Override
    public void set(@Nullable Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata) {
        if (object == null) {
            parameters.setNull(getSqlType());
        } else if (object instanceof Double) {
            parameters.setDouble((Double) object);
        } else {
            throw new IllegalArgumentException("To use the double variable type, the object needs to be a double!");
        }
    }

    @Override
    public @NotNull SQLType getSqlType() {
        return new SQLType() {
            @Override
            public String getName() {
                return "DOUBLE";
            }

            @Override
            public String getVendor() {
                return "codes.laivy.data.sql.natives.mysql.variable.type";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return JDBCType.DOUBLE.getVendorTypeNumber();
            }
        };
    }

    @Override
    public void configure(@NotNull SqlVariable variable) {
        variable.getDatabase().getManager().getVariablesManager().setType(variable, getSqlType());
    }
}
