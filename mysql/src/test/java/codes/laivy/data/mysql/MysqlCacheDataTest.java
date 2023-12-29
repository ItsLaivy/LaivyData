package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.data.Condition;
import codes.laivy.data.mysql.data.MysqlData;
import codes.laivy.data.mysql.data.MysqlDataCache;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.variable.MysqlVariable;
import codes.laivy.data.mysql.variable.type.provider.MysqlTextType;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MysqlCacheDataTest {

    public final @NotNull String USERNAME;
    public final @NotNull String PASSWORD;
    public final @NotNull InetAddress ADDRESS;
    public final int PORT;

    public MysqlCacheDataTest() throws Throwable {
        PASSWORD = "";
        USERNAME = "root";
        PORT = 3306;
        ADDRESS = InetAddress.getByName("localhost");
    }

    @Test
    public void testLoadAndUnload() throws Throwable {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);

        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull String expect = "Just a cool text :)";

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), expect);
        variable.start().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(variable.isNew());

        @NotNull MysqlVariable<String> variable2 = new MysqlVariable<>("id", table, new MysqlTextType(), null);
        variable2.start().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(variable2.isNew());

        // Data code
        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlDataCache cache = MysqlDataCache.copy(data);

        Assert.assertEquals(expect, data.get(variable));
        Assert.assertEquals(expect, cache.get(variable));
        data.stop(false).get(2, TimeUnit.SECONDS);
        // Test id
        data.start().get(2, TimeUnit.SECONDS);
        Assert.assertTrue(data.isNew());
        data.set(variable2, "test_id");
        data.stop(true).get(2, TimeUnit.SECONDS);
        Assert.assertTrue(data.exists().get(2, TimeUnit.SECONDS));
        
        cache = MysqlDataCache.retrieve(table, Condition.of(variable2, "test_id")).get(2, TimeUnit.SECONDS)[0];
        Assert.assertEquals(expect, cache.get(variable));
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testGlobalRetrieve() throws Exception {
        @NotNull Random random = new Random();
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);
        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        // Creating 4 datas
        int amount = random.nextInt(100);
        for (int row = 0; row < amount; row++) {
            @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
            data.save().get(2, TimeUnit.SECONDS);

            if (random.nextBoolean()) {
                data.start().get(2, TimeUnit.SECONDS);
            }
        }

        // Verifying if exists the 4 datas
        Assert.assertEquals((Integer) amount, (Integer) MysqlDataCache.retrieve(table).get(2, TimeUnit.SECONDS).length);
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testCacheRetrieving() throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);
        database.delete().get(2, TimeUnit.SECONDS);
        database.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test", table, new MysqlTextType(), null, true);
        variable.start().get(2, TimeUnit.SECONDS);

        // Creating 4 datas
        int amount = 10;
        for (int row = 1; row <= amount; row++) {
            @NotNull MysqlData data = MysqlData.create(table).join();
            data.start().join();
            data.set(variable, String.valueOf(row));
            data.stop(true).join();
        }

        int random = new Random().nextInt(10) + 1;
        @NotNull MysqlDataCache cache = Objects.requireNonNull(MysqlDataCache.retrieve(table, random).join());
        Assert.assertEquals(String.valueOf(random), cache.get(variable));
        //

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }
}
