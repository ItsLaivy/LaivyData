package tests;

import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.natives.*;
import codes.laivy.data.sql.mysql.natives.manager.MysqlManagerNative;
import codes.laivy.data.sql.mysql.variable.type.MysqlBooleanVariableType;

import java.sql.SQLException;

public class MysqlTests {
    public static void main(String[] args) {
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
            //database.delete();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
