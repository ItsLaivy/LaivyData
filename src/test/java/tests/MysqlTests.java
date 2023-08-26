package tests;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.natives.MysqlDatabaseNative;
import codes.laivy.data.sql.mysql.natives.MysqlReceptorNative;
import codes.laivy.data.sql.mysql.natives.MysqlTableNative;
import codes.laivy.data.sql.mysql.natives.MysqlVariableNative;
import codes.laivy.data.sql.mysql.natives.manager.MysqlManagerNative;
import codes.laivy.data.sql.mysql.variable.type.MysqlIntVariableType;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Objects;

public class MysqlTests {
    /**
     * Test the connection
     */
    @Test
    public void connection() throws SQLException {
        MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
        MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
        MysqlTable table = new MysqlTableNative(database, "table");
        SqlVariable var = new MysqlVariableNative(table, "var", new MysqlIntVariableType(), 0);
        MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
        receptor.load();

        // Check if is everything loaded
        Assert.assertTrue(database.isLoaded());
        Assert.assertTrue(table.isLoaded());
        Assert.assertTrue(var.isLoaded());
        Assert.assertTrue(receptor.isLoaded());
        // Check if receptor has variable 'var'
        receptor.get(var.getId());
        // Testing connection close
        database.unload(); // Is supposed to unload everything (table, receptor, variables)
        Assert.assertFalse(database.isLoaded());
        Assert.assertFalse(table.isLoaded());
        Assert.assertFalse(var.isLoaded());
        Assert.assertFalse(receptor.isLoaded());
    }

    /**
     * Test the database delete
     */
    @Test
    public void deletesDatabase() throws SQLException {
        MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
        MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
        MysqlTable table = new MysqlTableNative(database, "table");
        SqlVariable var = new MysqlVariableNative(table, "var", new MysqlIntVariableType(), 0);
        MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
        receptor.load();

        database.delete();

        Assert.assertFalse(database.isLoaded());
        Assert.assertFalse(table.isLoaded());
        Assert.assertFalse(var.isLoaded());
        Assert.assertFalse(receptor.isLoaded());
    }

    /**
     * Test the table delete
     */
    @Test
    public void deletesTable() throws SQLException {
        MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
        MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
        MysqlTable table = new MysqlTableNative(database, "table");
        SqlVariable var = new MysqlVariableNative(table, "var", new MysqlIntVariableType(), 0);
        MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
        receptor.load();

        table.delete();

        Assert.assertFalse(table.isLoaded());
        Assert.assertFalse(var.isLoaded());
        Assert.assertFalse(receptor.isLoaded());
    }

    /**
     * Test the variable delete
     */
    @Test
    public void deletesVariable() throws SQLException {
        MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
        MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
        MysqlTable table = new MysqlTableNative(database, "table");
        SqlVariable var = new MysqlVariableNative(table, "var", new MysqlIntVariableType(), 0);
        MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
        receptor.load();

        var.delete();

        Assert.assertFalse(var.isLoaded());
        Assert.assertEquals(0, receptor.getActiveContainers().size());
    }

    /**
     * Test the variables
     */
    @Test
    public void variables() throws SQLException {
        MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
        MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
        database.load();
        database.delete();
        database.load();

        MysqlTable table = new MysqlTableNative(database, "table");
        MysqlReceptor receptor = new MysqlReceptorNative(table, "test");
        receptor.load();
        receptor.delete();
        receptor.load();

        SqlVariable var = new MysqlVariableNative(table, "var", new MysqlIntVariableType(), 0);

        // Test new
        Assert.assertTrue(var.isNew());
        var.unload();
        var.load();
        Assert.assertFalse(var.isNew());
        // Test new

        receptor.get(var.getId());

        receptor.unload(false);
        receptor.load();
    }

    /**
     * Test the receptors
     */
    @Test
    public void receptors() throws SQLException {
        MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
        MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
        MysqlTable table = new MysqlTableNative(database, "table");
        SqlVariable var = new MysqlVariableNative(table, "var", new MysqlIntVariableType(), 0);
        MysqlReceptor receptor = new MysqlReceptorNative(table, "test");

        SqlVariable var2 = new MysqlVariableNative(table, "var2", new MysqlIntVariableType(), 0);
        receptor.load();
        receptor.delete();
        receptor.load();

        Assert.assertEquals(0, (int) Objects.requireNonNull(receptor.get(var2.getId())));
        receptor.get(var2.getId());

        receptor.set(var.getId(), (int) Objects.requireNonNull(receptor.get(var.getId())) + 1);
        receptor.reload(true);
        Assert.assertEquals(1, (int) Objects.requireNonNull(receptor.get(var.getId())));

        receptor.delete();
    }

    /**
     * Test the auto increment
     */
    @Test
    public void autoIncrement() throws SQLException {
        MysqlManagerNative manager = new MysqlManagerNative("localhost", "root", "", 3306);
        MysqlDatabase database = new MysqlDatabaseNative(manager, "test");
        MysqlTable table = new MysqlTableNative(database, "table_test_ai");

        table.delete();
        table.load();

        Assert.assertEquals(0, table.getAutoIncrement());

        MysqlReceptor receptor = new MysqlReceptorNative(table, "t0");
        receptor.load();
        receptor = new MysqlReceptorNative(table, "t1");
        receptor.load();
        receptor = new MysqlReceptorNative(table, "t2");
        receptor.load();
        receptor = new MysqlReceptorNative(table, "t3");
        receptor.load();
        receptor = new MysqlReceptorNative(table, "t4");
        receptor.load();

        Assert.assertEquals(5, table.getAutoIncrement());

        table.delete();
    }

}
