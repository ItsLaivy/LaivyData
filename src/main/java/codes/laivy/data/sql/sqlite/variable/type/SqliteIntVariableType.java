package codes.laivy.data.sql.sqlite.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import codes.laivy.data.sql.sqlite.variable.SqliteVariableType;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import codes.laivy.data.sql.variable.type.SqlNumberVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLType;

public class SqliteIntVariableType implements SqlNumberVariableType<SqliteVariable>, SqliteVariableType<SqliteVariable> {
    @Override
    public void set(@Nullable Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata) {
        if (object == null) {
            parameters.setNull(getSqlType());
        } else if (object instanceof Integer) {
            parameters.setInt((Integer) object);
        } else {
            throw new IllegalArgumentException("To use the int variable type, the object needs to be an integer!");
        }
    }

    @Override
    public @NotNull SQLType getSqlType() {
        return new SQLType() {
            @Override
            public String getName() {
                return "INTEGER";
            }

            @Override
            public String getVendor() {
                return "codes.laivy.data.sql.natives.sqlite.variable.type";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return 1;
            }
        };
    }

    @Override
    public void configure(@NotNull SqlVariable variable) {
        variable.getDatabase().getManager().getVariablesManager().setType(variable, getSqlType());
    }
}
