package codes.laivy.data.sql.manager;

import codes.laivy.data.api.manager.DatabaseManager;
import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.values.SqlConnection;
import org.jetbrains.annotations.NotNull;

public interface SqlManager<R extends SqlReceptor, V extends SqlVariable, D extends SqlDatabase, T extends SqlTable> extends DatabaseManager<R, V, D, T> {

    @Override
    @NotNull SqlVariablesManager<V> getVariablesManager();

    @Override
    @NotNull SqlReceptorsManager<R> getReceptorsManager();

    @Override
    @NotNull SqlTablesManager<T> getTablesManager();

}
