package codes.laivy.data.sql.mysql.natives;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.values.MysqlResultData;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.math.BigInteger;
import java.util.*;

/**
 * <p>
 *     The native MySQL Table of LaivyData.
 *     This native table autoload at constructor.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class MysqlTableNative implements MysqlTable {

    private final @NotNull MysqlDatabase database;
    private @NotNull String id;

    private final @NotNull Set<SqlReceptor> receptors = new LinkedHashSet<>();
    private final @NotNull Set<SqlVariable> variables = new LinkedHashSet<>();

    private boolean loaded = false;

    public MysqlTableNative(@NotNull MysqlDatabase database, @NotNull String id) {
        this(database, id, true);
    }
    public MysqlTableNative(@NotNull MysqlDatabase database, @NotNull String id, boolean autoLoad) {
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
        for (Receptor receptor : new LinkedHashSet<>(getLoadedReceptors())) {
            receptor.unload(true);
        }
        for (Variable variable : new LinkedHashSet<>(getLoadedVariables())) {
            variable.unload();
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
    public @NotNull MysqlDatabase getDatabase() {
        return database;
    }

    @Override
    public @Range(from = 0, to = Long.MAX_VALUE) long getAutoIncrement() {
        if (!getDatabase().isLoaded()) {
            throw new IllegalStateException("This database isn't loaded!");
        }

        MysqlResultStatement statement = getDatabase().getManager().getConnection().createStatement("SHOW TABLE STATUS FROM `" + getDatabase().getId() + "` LIKE '" + getId() + "'");
        MysqlResultData data = statement.execute();

        int code = 0;
        if (data != null) {
            code++;
            Optional<Map<String, Object>> optional = data.getValues().stream().findFirst();

            if (optional.isPresent()) {
                code++;
                Map<String, Object> map = optional.get();
                for (String key : map.keySet()) {
                    if (key.equalsIgnoreCase("auto_increment")) {
                        return ((BigInteger) map.get(key)).longValue() - 1L;
                    }
                }
            }
        }

        throw new IllegalStateException("Couldn't execute due to an unknown error: " + code);
    }
}
