package codes.laivy.data.sql.sqlite.manager;

import codes.laivy.data.sql.manager.SqlManager;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteReceptor;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface SqliteManager<R extends SqliteReceptor, V extends SqliteVariable, D extends SqliteDatabase, T extends SqliteTable> extends SqlManager<R, V, D, T> {

    @NotNull File getPath();

}
