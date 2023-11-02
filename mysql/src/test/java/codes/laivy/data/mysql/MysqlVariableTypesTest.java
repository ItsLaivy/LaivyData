package codes.laivy.data.mysql;

import codes.laivy.data.mysql.authentication.MysqlAuthentication;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.variable.MysqlVariable;
import codes.laivy.data.mysql.variable.type.MysqlBooleanType;
import codes.laivy.data.mysql.variable.type.MysqlTextType;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
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
                @NotNull MysqlVariable<String> variable = new MysqlVariable<>("test_var", table, new MysqlTextType(), "Just a cool test :)");
                variable.start().get(2, TimeUnit.SECONDS);
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
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }));
    }

}
