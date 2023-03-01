package tests;

import codes.laivy.data.api.variable.container.VariableContainer;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.natives.mysql.MysqlDatabase;
import codes.laivy.data.sql.natives.mysql.types.MysqlManagerImpl;
import codes.laivy.data.sql.natives.mysql.variable.type.MysqlByteVariableType;

import java.sql.SQLException;

public class MysqlTests {

    public static void main(String[] args) {
        try {
            MysqlManagerImpl manager = new MysqlManagerImpl("localhost", "root", "", 3306);

            MysqlDatabase database = new MysqlDatabase(manager, "test");
            database.load();

            SqlTable table = new SqlTable(database, "table");
            table.load();

            SqlVariable variable = new SqlVariable(table, new MysqlByteVariableType(database), "adadadadada", "var");
            variable.load();

            SqlReceptor receptor = new SqlReceptor(table, "test");
            receptor.load();
            for (VariableContainer var : receptor.getActiveContainers()) {
                System.out.println("Value: '" + var.get() + "'");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
