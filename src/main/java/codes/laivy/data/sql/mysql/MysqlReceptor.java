package codes.laivy.data.sql.mysql;

import codes.laivy.data.sql.SqlReceptor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public interface MysqlReceptor extends SqlReceptor {
    @Override
    @NotNull MysqlDatabase getDatabase();

    @Override
    @NotNull MysqlTable getTable();
}
