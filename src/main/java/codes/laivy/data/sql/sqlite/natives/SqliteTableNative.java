package codes.laivy.data.sql.sqlite.natives;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.table.Table;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteReceptor;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.values.SqliteResultData;
import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;

/**
 * <p>
 *     The native SQLite Table of LaivyData.
 *     This native table autoload at constructor.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class SqliteTableNative implements SqliteTable {

    private final @NotNull SqliteDatabase database;
    private @NotNull String id;

    private final @NotNull Set<SqlReceptor> receptors = new LinkedHashSet<>();
    private final @NotNull Set<SqlVariable> variables = new LinkedHashSet<>();

    private boolean loaded = false;

    public SqliteTableNative(@NotNull SqliteDatabase database, @NotNull String id) {
        this(database, id, true);
    }
    public SqliteTableNative(@NotNull SqliteDatabase database, @NotNull String id, boolean autoLoad) {
        this.database = database;
        this.id = id;

        if (autoLoad) {
            load();
        }
    }

    @Override
    public void load() {
        if (!getDatabase().isLoaded()) {
            throw new IllegalStateException("This database isn't loaded!");
        }

        getDatabase().getManager().getTablesManager().load(this);
        getDatabase().getLoadedTables().add(this);
        loaded = true;
    }

    @Override
    public void unload() {
        Map<SqliteTable, Set<SqliteReceptor>> tableMap = new HashMap<>();
        for (Receptor r : new LinkedHashSet<>(getLoadedReceptors())) {
            SqliteReceptor receptor = (SqliteReceptor) r;
            if (receptor.isLoaded()) {
                tableMap.putIfAbsent(receptor.getTable(), new HashSet<>());
                tableMap.get(receptor.getTable()).add(receptor);
            }
        }
        for (Map.Entry<SqliteTable, Set<SqliteReceptor>> entry : tableMap.entrySet()) {
            SqliteTable table = entry.getKey();
            table.getDatabase().getManager().getReceptorsManager().unload(entry.getValue().toArray(new SqliteReceptor[0]), true);
        }

        for (Variable variable : new LinkedHashSet<>(getLoadedVariables())) {
            if (variable.isLoaded()) variable.unload();
        }

        getDatabase().getManager().getTablesManager().unload(this);
        getDatabase().getLoadedTables().remove(this);
        loaded = false;
    }

    @Override
    public void delete() {
        unload();
        getDatabase().getManager().getTablesManager().delete(this);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public @NotNull Set<SqlReceptor> getLoadedReceptors() {
        return receptors;
    }

    @Override
    public @NotNull Set<SqlVariable> getLoadedVariables() {
        return variables;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull String id) {
        this.id = id;
    }

    @Override
    public @NotNull SqliteDatabase getDatabase() {
        return database;
    }

    @Override
    public @Range(from = 0, to = Long.MAX_VALUE) long getAutoIncrement() {
        if (!getDatabase().isLoaded()) {
            throw new IllegalStateException("This database isn't loaded!");
        }

        SqliteResultStatement statement = getDatabase().getConnection().createStatement("SELECT seq FROM sqlite_sequence WHERE name = '" + getId() + "'");
        SqliteResultData data = statement.execute();

        int code = 0;
        if (data != null) {
            code++;
            Optional<Map<String, Object>> optional = data.getValues().stream().findFirst();

            if (optional.isPresent()) {
                code++;
                Map<String, Object> map = optional.get();

                if (map.containsKey("seq")) {
                    return ((Integer) map.get("seq")).longValue();
                }
            } else {
                return 0L;
            }
        }

        throw new IllegalStateException("Couldn't execute due to an unknown error: " + code);
    }
}
