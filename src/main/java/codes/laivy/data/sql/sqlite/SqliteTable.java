package codes.laivy.data.sql.sqlite;

import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqliteTable extends SqlTable {
    @Override
    @NotNull SqliteDatabase getDatabase();

    @Override
    default @Nullable SqliteVariable getLoadedVariable(@NotNull String id) {
        for (SqlVariable table : getLoadedVariables()) {
            if (table.getId().equals(id)) {
                return (SqliteVariable) table;
            }
        }
        return null;
    }
}
