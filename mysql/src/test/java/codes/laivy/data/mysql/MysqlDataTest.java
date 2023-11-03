package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.data.Condition;
import codes.laivy.data.mysql.data.MysqlData;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.variable.MysqlVariable;
import codes.laivy.data.mysql.variable.type.MysqlTextType;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class MysqlDataTest {

    public final @NotNull String USERNAME;
    public final @NotNull String PASSWORD;
    public final @NotNull InetAddress ADDRESS;
    public final int PORT;

    public MysqlDataTest() throws Throwable {
        PASSWORD = "";
        USERNAME = "root";
        PORT = 3306;
        ADDRESS = InetAddress.getByName("localhost");
    }

    @Test
    public void testLoadAndUnload() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), null);
        variable.start().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        Assert.assertFalse(data.exists().get(2, TimeUnit.SECONDS));

        data.start().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(data.exists().get(2, TimeUnit.SECONDS));
        Assert.assertTrue(data.isLoaded());

        data.stop(true).get(2, TimeUnit.SECONDS);
        Assert.assertTrue(data.exists().get(2, TimeUnit.SECONDS));
        data.start().get(2, TimeUnit.SECONDS);
        //

        database.delete().get(2, TimeUnit.SECONDS);

        Assert.assertFalse(data.exists().get(2, TimeUnit.SECONDS));
        Assert.assertFalse(data.isLoaded());

        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testValueSet() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), null);
        variable.start().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);

        @NotNull String expected = "Just a cool test :)";
        data.set(variable, expected);
        data.stop(true).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        Assert.assertEquals(expected, data.get(variable));
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testDefaultValue() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull String expected = "Just a cool test :)";

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), expected);
        variable.start().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(data.isNew());
        Assert.assertEquals(expected, data.get(variable));
        data.stop(true).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(data.isNew());
        Assert.assertEquals(expected, data.get(variable));
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testWithoutVariables() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        data.stop(true).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testCondition() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull String expected = "Just a cool test :)";

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), expected);
        variable.start().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        long row = data.getRow();

        Assert.assertTrue(data.isNew());
        Assert.assertEquals(expected, data.get(variable));
        data.stop(true).get(2, TimeUnit.SECONDS);
        //

        @NotNull MysqlData[] datas = MysqlData.retrieve(table, Condition.of(variable, expected)).get(2, TimeUnit.SECONDS);
        Assert.assertTrue(datas.length == 1 && datas[0].getRow() == row);

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testDelete() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        data.save().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(data.exists().get(2, TimeUnit.SECONDS));
        data.delete().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(data.exists().get(2, TimeUnit.SECONDS));
        Assert.assertFalse(data.isLoaded());
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testAddVariableAfterDataLoading() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);

        @NotNull String expected = "Just a cool test :)";
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), expected);
        variable.start().get(2, TimeUnit.SECONDS);

        Assert.assertEquals(data.get(variable), expected);
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testStartVariableAfterDataLoading() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        @NotNull String expected = "Just a cool test :)";
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), expected);
        variable.start().get(2, TimeUnit.SECONDS);
        variable.stop().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);

        variable.start().get(2, TimeUnit.SECONDS);

        Assert.assertEquals(data.get(variable), expected);
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testExistsWithCondition() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");

        database.start().get(2, TimeUnit.SECONDS);
        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        @NotNull String expected = "Just a cool test :)";
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), expected);
        variable.start().get(2, TimeUnit.SECONDS);

        // Creating 4 datas
        MysqlData.create(table).get(2, TimeUnit.SECONDS).start().get(2, TimeUnit.SECONDS);
        MysqlData.create(table).get(2, TimeUnit.SECONDS).start().get(2, TimeUnit.SECONDS);
        MysqlData.create(table).get(2, TimeUnit.SECONDS).start().get(2, TimeUnit.SECONDS);
        // I'll unload that one just to improve test accuracy
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        data.stop(true).get(2, TimeUnit.SECONDS);
        // Verifying if exists the 4 datas
        Assert.assertEquals((Integer) 4, MysqlData.exists(table, Condition.of(variable, expected)).get(2, TimeUnit.SECONDS));
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }
}
