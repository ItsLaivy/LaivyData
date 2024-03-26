package sqlite;

import codes.laivy.data.sqlite.SqliteData;
import codes.laivy.data.sqlite.database.SqliteDatabase;
import codes.laivy.data.sqlite.table.SqliteTable;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class SqliteTableTest {

    private final @NotNull File DATABASE_FILE = new File("./test.db");

    @Test
    public void loadAndUnload() throws Exception {
        @NotNull SqliteDatabase database = SqliteDatabase.getOrCreate(DATABASE_FILE);
        Assert.assertTrue(database.start().join());

        // Table code
        SqliteTable table = new SqliteTable("test_table", database);
        if (table.exists().get()) table.delete().get();

        table.start().get(2, TimeUnit.SECONDS);

        Assert.assertTrue(table.isLoaded());
        Assert.assertTrue(table.isNew());

        table.stop().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(table.isLoaded());

        table.start().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(table.isNew());
        //

        database.delete().get(2, TimeUnit.SECONDS);
        Assert.assertFalse(table.isLoaded());
    }

    @Test
    public void delete() throws Exception {
        @NotNull SqliteDatabase database = SqliteDatabase.getOrCreate(DATABASE_FILE);
        Assert.assertTrue(database.start().join());

        // Table code
        SqliteTable table = new SqliteTable("test_table", database);
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
    }
    @Test
    public void testAutoIncrement() throws Exception {
        @NotNull SqliteDatabase database = SqliteDatabase.getOrCreate(DATABASE_FILE);
        Assert.assertTrue(database.start().join());

        // Table code
        SqliteTable table = new SqliteTable("test_table", database);
        table.start().get(2, TimeUnit.SECONDS);
        Assert.assertEquals(1, (int) table.getAutoIncrement().getAmount().get(2, TimeUnit.SECONDS));

        @NotNull SqliteData data = SqliteData.create(table).get(2, TimeUnit.SECONDS);
        data.start().get(2, TimeUnit.SECONDS);
        data.stop(true).get(2, TimeUnit.SECONDS);

        Assert.assertEquals(2, (int) table.getAutoIncrement().getAmount().get(2, TimeUnit.SECONDS));
        //

        database.delete().get(2, TimeUnit.SECONDS);
    }

//    @Test
//    public void testRows() throws Exception {
//        @NotNull MysqlAuthentication authentication = new MysqlAuthentication(USERNAME, PASSWORD, ADDRESS, PORT);
//        authentication.connect().get(5, TimeUnit.SECONDS);
//        @NotNull MysqlDatabase database = MysqlDatabase.getOrCreate(authentication, "test");
//        database.start().get(2, TimeUnit.SECONDS);
//
//        // Table code
//        MysqlTable table = new MysqlTable("test_table", database);
//        table.start().get(2, TimeUnit.SECONDS);
//        Assert.assertEquals((Long) 0L, table.getRows().get(2, TimeUnit.SECONDS));
//
//        @NotNull MysqlData data = MysqlData.create(table).get(2, TimeUnit.SECONDS);
//        data.start().get(2, TimeUnit.SECONDS);
//        data.stop(true).get(2, TimeUnit.SECONDS);
//
//        Assert.assertEquals((Long) 1L, table.getRows().get(2, TimeUnit.SECONDS));
//        //
//
//        database.delete().get(2, TimeUnit.SECONDS);
//        authentication.disconnect().get(5, TimeUnit.SECONDS);
//    }

}
