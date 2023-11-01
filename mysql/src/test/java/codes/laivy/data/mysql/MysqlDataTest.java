package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.data.MysqlData;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.variable.MysqlVariable;
import codes.laivy.data.mysql.variable.type.MysqlTinyTextType;
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
        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTinyTextType(), null);
        variable.start().get(2, TimeUnit.SECONDS);

        // Data code
        @NotNull MysqlData data = MysqlData.retrieve(table, 1);
        data.start().join();
        //

        database.delete().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(variable.isLoaded());

        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testDelete() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);
        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        // Table code
        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTinyTextType(), null);
        variable.start().get(2, TimeUnit.SECONDS);

        Assert.assertTrue(variable.isLoaded());
        Assert.assertTrue(variable.exists().get(2, TimeUnit.SECONDS));

        variable.delete().get(2, TimeUnit.SECONDS);

        Assert.assertFalse(variable.isLoaded());
        Assert.assertFalse(variable.exists().get(2, TimeUnit.SECONDS));

        variable.start().get(2, TimeUnit.SECONDS);
        //

        database.delete().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(variable.isLoaded());

        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }
}
