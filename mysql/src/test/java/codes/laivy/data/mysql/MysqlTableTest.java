package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.data.MysqlData;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class MysqlTableTest {

    public final @NotNull String USERNAME;
    public final @NotNull String PASSWORD;
    public final @NotNull InetAddress ADDRESS;
    public final int PORT;

    public MysqlTableTest() throws Throwable {
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

        // Table code
        MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(table.isLoaded());
        Assert.assertTrue(table.isNew());

        table.getAutoIncrement().setAmount(100).get(2, TimeUnit.SECONDS);
        Assert.assertEquals((Long) 100L, table.getAutoIncrement().getAmount().get(2, TimeUnit.SECONDS));

        table.stop().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(table.isLoaded());

        table.start().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(table.isNew());
        //

        database.delete().get(2, TimeUnit.SECONDS);

        Assert.assertFalse(table.isLoaded());

        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testDelete() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        // Table code
        MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        table.delete().get(2, TimeUnit.SECONDS);

        Assert.assertFalse(table.isLoaded());
        Assert.assertFalse(table.exists().get(2, TimeUnit.SECONDS));

        table.start().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(table.isLoaded());
        Assert.assertTrue(table.exists().get(2, TimeUnit.SECONDS));
        //

        database.delete().get(2, TimeUnit.SECONDS);

        Assert.assertFalse(table.isLoaded());
        Assert.assertFalse(table.exists().get(2, TimeUnit.SECONDS));

        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }
    @Test
    public void testAutoIncrement() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        // Table code
        MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        Assert.assertEquals((Long) 1L, table.getAutoIncrement().getAmount().get(2, TimeUnit.SECONDS));

        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        data.stop(true).get(2, TimeUnit.SECONDS);

        Assert.assertEquals((Long) 2L, table.getAutoIncrement().getAmount().get(2, TimeUnit.SECONDS));
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }
    @Test
    public void testRows() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        // Table code
        MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        Assert.assertEquals((Long) 0L, table.getRows().get(2, TimeUnit.SECONDS));

        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        data.stop(true).get(2, TimeUnit.SECONDS);

        Assert.assertEquals((Long) 1L, table.getRows().get(2, TimeUnit.SECONDS));
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

}
