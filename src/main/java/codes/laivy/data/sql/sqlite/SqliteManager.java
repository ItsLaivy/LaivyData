package codes.laivy.data.sql.sqlite;

import codes.laivy.data.sql.manager.SqlManager;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteReceptor;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqliteManager<R extends SqliteReceptor, V extends SqliteVariable, D extends SqliteDatabase, T extends SqliteTable> extends SqlManager<R, V, D, T> {

    /**
     * The database path of the databases using this manager
     * 
     * @return the path of this manager's databases
     * @author Laivy
     * @since 1.0
     */
    @NotNull File getPath();

}
