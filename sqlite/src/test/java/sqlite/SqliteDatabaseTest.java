package sqlite;

import codes.laivy.data.sqlite.database.SqliteDatabase;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class SqliteDatabaseTest {

    private final @NotNull File DATABASE_FILE = new File("./test.db");

    @Test
    public void loadAndUnload() throws Exception {
        @NotNull SqliteDatabase database = SqliteDatabase.getOrCreate(DATABASE_FILE);
        Assert.assertTrue(database.start().join());
        Assert.assertTrue(database.isLoaded());

        Assert.assertTrue(DATABASE_FILE.exists());
        database.stop().join();
        Assert.assertFalse(database.isLoaded());
    }

    @Test
    public void delete() throws Exception {
        @NotNull SqliteDatabase database = SqliteDatabase.getOrCreate(DATABASE_FILE);
        Assert.assertTrue(database.start().join());
        Assert.assertTrue(DATABASE_FILE.exists());

        database.stop().join();

        database.delete().join();
        Assert.assertFalse(DATABASE_FILE.exists());

        // With loaded
    }

}
