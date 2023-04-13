package tests;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteReceptor;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.natives.SqliteDatabaseNative;
import codes.laivy.data.sql.sqlite.natives.SqliteReceptorNative;
import codes.laivy.data.sql.sqlite.natives.SqliteTableNative;
import codes.laivy.data.sql.sqlite.natives.SqliteVariableNative;
import codes.laivy.data.sql.sqlite.natives.manager.SqliteManagerNative;
import codes.laivy.data.sql.sqlite.variable.type.SqliteByteVariableType;

import java.io.File;

public class SqliteTests {
    public static void main(String[] args) {
        SqliteManagerNative manager = new SqliteManagerNative(new File("."));
        SqliteDatabase database = new SqliteDatabaseNative(manager, "test");
        SqliteTable table = new SqliteTableNative(database, "table");

        SqlVariable var = new SqliteVariableNative(table, "var", new SqliteByteVariableType(), null);

        SqliteReceptor receptor = new SqliteReceptorNative(table, "test");
        receptor.load();

        receptor.set("var", true);
        System.out.println(receptor.get("var").toString());

        receptor.save();
        //receptor.delete();
        //var.delete();
        //table.delete();
        database.delete();
    }
}
