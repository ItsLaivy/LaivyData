package tests;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteReceptor;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.natives.SqliteDatabaseNative;
import codes.laivy.data.sql.sqlite.natives.SqliteReceptorNative;
import codes.laivy.data.sql.sqlite.natives.SqliteTableNative;
import codes.laivy.data.sql.sqlite.natives.SqliteVariableNative;
import codes.laivy.data.sql.sqlite.natives.manager.SqliteManagerNative;
import codes.laivy.data.sql.sqlite.variable.type.SqliteIntVariableType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

public class SqliteTests {

    /**
     * Test the connection
     */
    @Test
    public void connection() {
        SqliteManagerNative manager = new SqliteManagerNative(new File("."));
        SqliteDatabase database = new SqliteDatabaseNative(manager, "test");
        SqliteTable table = new SqliteTableNative(database, "table");
        SqlVariable var = new SqliteVariableNative(table, "var", new SqliteIntVariableType());
        SqliteReceptor receptor = new SqliteReceptorNative(table, "test");
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
    public void deletesDatabase() {
        SqliteManagerNative manager = new SqliteManagerNative(new File("."));
        SqliteDatabase database = new SqliteDatabaseNative(manager, "test");
        SqliteTable table = new SqliteTableNative(database, "table");
        SqlVariable var = new SqliteVariableNative(table, "var", new SqliteIntVariableType());
        SqliteReceptor receptor = new SqliteReceptorNative(table, "test");
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
    public void deletesTable() {
        SqliteManagerNative manager = new SqliteManagerNative(new File("."));
        SqliteDatabase database = new SqliteDatabaseNative(manager, "test");
        SqliteTable table = new SqliteTableNative(database, "table");
        SqlVariable var = new SqliteVariableNative(table, "var", new SqliteIntVariableType());
        SqliteReceptor receptor = new SqliteReceptorNative(table, "test");
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
    public void deletesVariable() {
        SqliteManagerNative manager = new SqliteManagerNative(new File("."));
        SqliteDatabase database = new SqliteDatabaseNative(manager, "test");
        SqliteTable table = new SqliteTableNative(database, "table");
        SqlVariable var = new SqliteVariableNative(table, "var", new SqliteIntVariableType());
        SqliteReceptor receptor = new SqliteReceptorNative(table, "test");
        receptor.load();

        var.delete();

        Assert.assertFalse(var.isLoaded());
        Assert.assertEquals(0, receptor.getActiveContainers().size());
    }

    /**
     * Test the variables
     */
    @Test
    public void variables() {
        SqliteManagerNative manager = new SqliteManagerNative(new File("."));
        SqliteDatabase database = new SqliteDatabaseNative(manager, "test");
        SqliteTable table = new SqliteTableNative(database, "table");
        SqliteReceptor receptor = new SqliteReceptorNative(table, "test");
        receptor.load();
        receptor.delete();
        receptor.load();

        SqlVariable var = new SqliteVariableNative(table, "var", new SqliteIntVariableType());

        receptor.get(var.getId());

        receptor.unload(false);
        receptor.load();
    }

    /**
     * Test the receptors
     */
    @Test
    public void receptors() {
        SqliteManagerNative manager = new SqliteManagerNative(new File("."));
        SqliteDatabase database = new SqliteDatabaseNative(manager, "test");

        SqliteTable table = new SqliteTableNative(database, "table");
        SqlVariable var = new SqliteVariableNative(table, "var", new SqliteIntVariableType());
        SqliteReceptor receptor = new SqliteReceptorNative(table, "test");
        receptor.load();

        SqlVariable var2 = new SqliteVariableNative(table, "var2", new SqliteIntVariableType());
        receptor.set(var.getId(), 0);
        receptor.reload(true);

        receptor.get(var2.getId());

        receptor.set(var.getId(), (int) Objects.requireNonNull(receptor.get(var.getId())) + 1);
        receptor.reload(true);
        Assert.assertEquals(1, (int) Objects.requireNonNull(receptor.get(var.getId())));

        receptor.delete();
    }

}
