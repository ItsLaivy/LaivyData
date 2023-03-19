package tests;

import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.natives.*;
import codes.laivy.data.sql.mysql.natives.manager.MysqlManagerNative;
import codes.laivy.data.sql.mysql.variable.type.*;

import java.util.Objects;

public class MysqlTests {
    public static void main(String[] args) {
        testMysql();
        testNumbers();
        testEnum();
    }

    public static void testMysql() {
        try {
            MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "password", 3306);
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
            MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "password", 3306);
            MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
            MysqlTable table = new MysqlTableNative(database, "table");

            MysqlVariable var = new MysqlVariableNative(table, "enum", new MysqlEnumVariableType<>(TestEnum.class), TestEnum.VALOR_1);

            MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
            receptor.load();

            System.out.println("Enum value: '" + ((TestEnum) Objects.requireNonNull(receptor.get("enum"))).name() + "'");

            receptor.save();
            receptor.delete();
            var.delete();
            table.delete();
            database.delete();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void testNumbers() {
        try {
            MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "password", 3306);
            MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
            MysqlTable table = new MysqlTableNative(database, "table");

            MysqlVariable integerVar = new MysqlVariableNative(table, "integer", new MysqlIntVariableType(), 0);
            MysqlVariable doubleVar = new MysqlVariableNative(table, "double", new MysqlDoubleVariableType(), 0D);
            MysqlVariable floatVar = new MysqlVariableNative(table, "float", new MysqlFloatVariableType(), 0F);
            MysqlVariable longVar = new MysqlVariableNative(table, "long", new MysqlLongVariableType(), 0L);

            MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
            receptor.load();

            receptor.save();
            receptor.delete();

//            integerVar.delete();
//            doubleVar.delete();
//            floatVar.delete();
//            longVar.delete();
//
//            table.delete();
//            database.delete();
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
