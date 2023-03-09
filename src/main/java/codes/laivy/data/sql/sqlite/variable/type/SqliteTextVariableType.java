package codes.laivy.data.sql.sqlite.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import codes.laivy.data.sql.sqlite.variable.SqliteVariableType;
import codes.laivy.data.sql.variable.type.SqlTextVariableType;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLType;

/**
 * Converts a object into string using the {@link Object#toString()} method, and saves at the database using the TEXT column data type.
 * 
 * @author Laivy
 * @since 1.0
 */
public class SqliteTextVariableType implements SqlTextVariableType<SqliteVariable>, SqliteVariableType<SqliteVariable> {

    protected @NotNull SQLType type;

    public SqliteTextVariableType() {
        type = new SQLType() {
            @Override
            public String getName() {
                return "TEXT";
            }

            @Override
            public String getVendor() {
                return "codes.laivy.data.sql.sqlite.variable.type";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return 3;
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
}
