package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.database.MysqlDatabase;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class MysqlDatabaseTest {

    public final @NotNull String USERNAME;
    public final @NotNull String PASSWORD;
    public final @NotNull InetAddress ADDRESS;
    public final int PORT;

    public MysqlDatabaseTest() throws Throwable {
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

        Assert.assertTrue(database.isNew());
        Assert.assertTrue(database.exists().get(1, TimeUnit.SECONDS));
        Assert.assertTrue(database.isLoaded());

        database.stop().get(5, TimeUnit.SECONDS);
        Assert.assertFalse(database.isLoaded());

        database.start().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(database.isLoaded());
        Assert.assertFalse(database.isNew());

        authentication.disconnect().get(5, TimeUnit.SECONDS);

        Assert.assertFalse(database.isLoaded());
    }

    @Test
    public void testDelete() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.delete().get(5, TimeUnit.SECONDS);

        Assert.assertFalse(database.isLoaded());
        Assert.assertFalse(database.exists().get(2, TimeUnit.SECONDS));

        database.start().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(database.isLoaded());

        authentication.disconnect().get(5, TimeUnit.SECONDS);

        Assert.assertFalse(database.isLoaded());
    }

}
