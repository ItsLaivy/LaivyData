package codes.laivy.data.sql.sqlite;

import codes.laivy.data.sql.SqlReceptor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqliteReceptor extends SqlReceptor {
    @Override
    @NotNull SqliteDatabase getDatabase();

    @Override
    @NotNull SqliteTable getTable();
}
