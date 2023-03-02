package codes.laivy.data.sql.sqlite.variable.type;

import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import codes.laivy.data.sql.sqlite.variable.SqliteVariableType;
import codes.laivy.data.sql.variable.type.SqlTextVariableType;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLType;

public class SqliteTextVariableType implements SqlTextVariableType<SqliteVariable>, SqliteVariableType<SqliteVariable> {

    private final @NotNull SqliteDatabase database;
    protected @NotNull SQLType type;

    public SqliteTextVariableType(@NotNull SqliteDatabase database) {
        this.database = database;

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
    public @NotNull SqliteDatabase getDatabase() {
        return database;
    }

    @Override
    public @NotNull SQLType getSqlType() {
        return type;
    }

    @Override
    public void configure(@NotNull SqliteVariable variable) {
        variable.getDatabase().getManager().getVariablesManager().setType(variable, getSqlType());
    }
}
