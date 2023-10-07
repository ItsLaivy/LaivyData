import codes.laivy.data.MysqlAuthentication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@TestOnly
public class Authentication {

    public final @NotNull String USERNAME;
    public final @NotNull String PASSWORD;
    public final @NotNull InetAddress ADDRESS;
    public final int PORT;

    public Authentication() {
        PASSWORD = "";
        USERNAME = "root";
        PORT = 3306;

        try {
            ADDRESS = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void connectAndDisconnect() throws Exception {
        MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);

        Assert.assertNotNull("Cannot load authentication connection", authentication.getConnection());
        Assert.assertFalse("Cannot validate authentication", authentication.getConnection().isClosed());

        authentication.disconnect().get(5, TimeUnit.SECONDS);

        Assert.assertNull("Cannot unload authentication connection", authentication.getConnection());
    }

}
