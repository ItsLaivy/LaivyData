package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
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

        table.stop().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(table.isLoaded());
        //

        database.delete().get(2, TimeUnit.SECONDS);
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

}
