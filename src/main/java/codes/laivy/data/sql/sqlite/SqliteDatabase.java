package codes.laivy.data.sql.sqlite;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.sqlite.connection.SqliteConnection;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqliteDatabase extends SqlDatabase {
    
    @Override
    @NotNull SqliteConnection getConnection();

    @Override
    @Pattern("^[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9]+)?$")
    @NotNull String getId();

    /**
     * Gets the file of this database
     *
     * @return the database file
     * @author Laivy
     * @since 1.0
     */
    @NotNull File getFile();
    
    @Override
    @NotNull SqliteManager<SqliteReceptor, SqliteVariable, SqliteDatabase, SqliteTable> getManager();
}
