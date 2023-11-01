package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.MysqlVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@TestOnly
public class AuthenticationTest {

    public final @NotNull String USERNAME;
    public final @NotNull String PASSWORD;
    public final @NotNull InetAddress ADDRESS;
    public final int PORT;

    public AuthenticationTest() throws Throwable {
        PASSWORD = "";
        USERNAME = "root";
        PORT = 3306;
        ADDRESS = InetAddress.getByName("localhost");
    }

    @Test
    public void connectAndDisconnect() throws Exception {
        MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);

        Assert.assertNotNull("Cannot load authentication connection", authentication.getConnection());
        Assert.assertNotNull("Cannot load mysql server version", authentication.getVersion());
        Assert.assertFalse("Cannot validate authentication", authentication.getConnection().isClosed());

        @NotNull MysqlVersion version = authentication.getVersion();
        if (version.getMajor() < 5 || version.getMinor() < 1) {
            throw new IllegalStateException("The LaivyData-Java/Mysql has only compatible with MySQL 5.1 or higher");
        }

        authentication.disconnect().get(5, TimeUnit.SECONDS);
        Assert.assertNull("Cannot unload authentication connection", authentication.getConnection());
    }

}
