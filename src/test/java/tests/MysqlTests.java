package tests;

import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.natives.*;
import codes.laivy.data.sql.mysql.natives.manager.MysqlManagerNative;
import codes.laivy.data.sql.mysql.variable.type.MysqlBooleanVariableType;
import codes.laivy.data.sql.mysql.variable.type.MysqlEnumVariableType;

import java.util.Objects;

public class MysqlTests {
    public static void main(String[] args) {
        testEnum();
    }

    public static void testMysql() {
        try {
            MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
            MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
            MysqlTable table = new MysqlTableNative(database, "table");

            MysqlVariable var = new MysqlVariableNative(table, "var", new MysqlBooleanVariableType(), true);

            MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
            receptor.load();

            receptor.set("var", false);

            receptor.save();
            receptor.delete();
            var.delete();
            table.delete();
            database.delete();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void testEnum() {
        try {
            MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
            MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
            MysqlTable table = new MysqlTableNative(database, "table");

            MysqlVariable var = new MysqlVariableNative(table, "var", new MysqlEnumVariableType<>(TestEnum.class), TestEnum.VALOR_1);

            MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
            receptor.load();

            System.out.println("Enum value: '" + ((TestEnum) Objects.requireNonNull(receptor.get("var"))).name() + "'");

            receptor.save();
            receptor.delete();
            var.delete();
            table.delete();
            database.delete();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public enum TestEnum {
        VALOR_1,
        VALOR_2,
        VALOR_3,
        ;
    }

}
