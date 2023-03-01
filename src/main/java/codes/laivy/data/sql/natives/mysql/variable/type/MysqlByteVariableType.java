package codes.laivy.data.sql.natives.mysql.variable.type;

import codes.laivy.data.sql.natives.mysql.MysqlDatabase;
import codes.laivy.data.sql.variable.type.SqlByteVariableType;
import org.jetbrains.annotations.NotNull;

import java.sql.JDBCType;
import java.sql.SQLType;

public class MysqlByteVariableType implements SqlByteVariableType {

    private final @NotNull MysqlDatabase database;
    protected @NotNull SQLType type;

    public MysqlByteVariableType(@NotNull MysqlDatabase database) {
        this.database = database;

        type = new SQLType() {
            @Override
            public String getName() {
                return "BLOB";
            }

            @Override
            public String getVendor() {
                return "codes.laivy.data.sql.natives.mysql.variable.type";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return JDBCType.BLOB.getVendorTypeNumber();
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

}
