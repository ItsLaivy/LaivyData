package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.data.MysqlData;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.variable.MysqlVariable;
import codes.laivy.data.mysql.variable.type.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MysqlVariableTypesTest {

    public final @NotNull String USERNAME;
    public final @NotNull String PASSWORD;
    public final @NotNull InetAddress ADDRESS;
    public final int PORT;

    public MysqlVariableTypesTest() throws Throwable {
        PASSWORD = "";
        USERNAME = "root";
        PORT = 3306;
        ADDRESS = InetAddress.getByName("localhost");
    }

    private void generate(@NotNull Consumer<MysqlTable> consumer) throws Exception {
        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
        authentication.connect().get(5, TimeUnit.SECONDS);
        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
        database.start().get(2, TimeUnit.SECONDS);
        @NotNull MysqlTable table = new MysqlTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);

        consumer.accept(table);

        database.delete().get(2, TimeUnit.SECONDS);
        authentication.disconnect().get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testTextType() throws Exception {
        generate((table -> {
            try {
                @NotNull String expected = "Just a cool test :)";

                @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), expected);
                variable.start().get(2, TimeUnit.SECONDS);

                @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
                data.start().get(2, TimeUnit.SECONDS);
                data.save().get(2, TimeUnit.SECONDS);

                Assert.assertEquals(expected, data.get(variable));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }));
    }
    @Test
    public void testBooleanType() throws Exception {
        generate((table -> {
            try {
                @NotNull MysqlVariable<Boolean> variable = new MysqlVariable<>("test_var", table, new MysqlBooleanType(), true);
                variable.start().get(2, TimeUnit.SECONDS);

                @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
                data.start().get(2, TimeUnit.SECONDS);
                data.save().get(2, TimeUnit.SECONDS);

                Assert.assertEquals(true, data.get(variable));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }));
    }
    @Test
    public void testBlobType() throws Exception {
        generate((table -> {
            try {
                byte[] expected = "abc".getBytes(StandardCharsets.UTF_8);

                @NotNull MysqlVariable<byte[]> variable = new MysqlVariable<>("test_var", table, new MysqlBlobType(), expected);
                variable.start().get(2, TimeUnit.SECONDS);

                @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
                data.start().get(2, TimeUnit.SECONDS);
                data.save().get(2, TimeUnit.SECONDS);

                Assert.assertEquals(expected, data.get(variable));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }));
    }
    @Test
    public void testDoubleType() throws Exception {
        generate((table -> {
            try {
                double expected = 100D;

                @NotNull MysqlVariable<Double> variable = new MysqlVariable<>("test_var", table, new MysqlDoubleType(), expected);
                variable.start().get(2, TimeUnit.SECONDS);

                @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
                data.start().get(2, TimeUnit.SECONDS);
                data.save().get(2, TimeUnit.SECONDS);

                Assert.assertEquals((Double) expected, data.get(variable));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }));
    }
    @Test
    public void testFloatType() throws Exception {
        generate((table -> {
            try {
                float expected = 100F;

                @NotNull MysqlVariable<Float> variable = new MysqlVariable<>("test_var", table, new MysqlFloatType(), expected);
                variable.start().get(2, TimeUnit.SECONDS);

                @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
                data.start().get(2, TimeUnit.SECONDS);
                data.save().get(2, TimeUnit.SECONDS);

                Assert.assertEquals((Float) expected, data.get(variable));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }));
    }

}
