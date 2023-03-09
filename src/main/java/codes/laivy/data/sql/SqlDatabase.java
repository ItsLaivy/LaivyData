package codes.laivy.data.sql;

import codes.laivy.data.api.database.Database;
import codes.laivy.data.sql.manager.SqlManager;
import codes.laivy.data.sql.values.SqlConnection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqlDatabase extends Database {

    @Contract(pure = true)
    @NotNull SqlConnection getConnection();

    /**
     * This set <b>MUST</b> contains only loaded tables
     * @return all loaded tables at this database
     *
     * @author Laivy
     * @since 1.0
     */
    @NotNull Set<SqlTable> getLoadedTables();

    @Override
    @Contract(pure = true)
    @NotNull String getId();

    @Override
    @Contract(pure = true)
    @NotNull SqlManager<?, ?, ?, ?> getManager();
}
