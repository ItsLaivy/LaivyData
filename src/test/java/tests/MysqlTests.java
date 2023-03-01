package tests;

import codes.laivy.data.api.variable.container.VariableContainer;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.natives.*;
import codes.laivy.data.sql.mysql.natives.manager.MysqlManagerNative;
import codes.laivy.data.sql.mysql.variable.type.MysqlTextVariableType;

import java.sql.SQLException;

public class MysqlTests {

    public static void main(String[] args) {
        try {
            MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
            MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
            MysqlTable table = new MysqlTableNative(database, "table");

            new MysqlVariableNative(table, "var", new MysqlTextVariableType(database, MysqlTextVariableType.Size.TINYTEXT), "a");

            MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
            receptor.load();
            
            for (VariableContainer var : receptor.getActiveContainers()) {
                System.out.println("Value: '" + var.get() + "'");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
