package codes.laivy.data.sql.sqlite.natives;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteReceptor;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.regex.Pattern.matches;

/**
 * <p>
 *     The native SQLite Receptor of LaivyData.
 *     This native receptor doesn't autoload, you need to load yourself.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class SqliteReceptorNative implements SqliteReceptor {

    private final @NotNull SqliteTable table;
    private @NotNull String id;

    private @Range(from = 0, to = Long.MAX_VALUE) int index;

    protected boolean isNew = false;

    private final @NotNull Set<InactiveVariableContainer> inactiveVariableContainers = new LinkedHashSet<>();
    private final @NotNull Set<ActiveVariableContainer> activeVariableContainers = new LinkedHashSet<>();

    public SqliteReceptorNative(@NotNull SqliteTable table, @NotNull String id) {
        this.table = table;
        this.id = id;

        if (!matches("^.{0,128}$", id)) {
            throw new IllegalArgumentException("The receptor id must follow the regex '^.{0,128}$'");
        }
    }

    @Override
    public void load() {
        getDatabase().getManager().getReceptorsManager().load(this);
    }

    @Override
    public void save() {
        getDatabase().getManager().getReceptorsManager().save(this);
    }

    @Override
    public void unload(boolean save) {
        getDatabase().getManager().getReceptorsManager().unload(this, save);
    }

    @Override
    public boolean isLoaded() {
        return getDatabase().getManager().getReceptorsManager().isLoaded(this);
    }

    @Override
    public void delete() {
        getDatabase().getManager().getReceptorsManager().delete(this);
    }

    /**
     * This set is cleared when a receptor loads/unloads, contains the inactive variable containers.
     * @return a set has all inactive variable container
     */
    @Override
    public @NotNull Set<@NotNull InactiveVariableContainer> getInactiveContainers() {
        return inactiveVariableContainers;
    }

    /**
     * This set is cleared when a receptor loads/unloads, contains the active variable containers.
     * @return a set has all active variable container
     */
    @Override
    public @NotNull Set<@NotNull ActiveVariableContainer> getActiveContainers() {
        return activeVariableContainers;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull String id) {
        getDatabase().getManager().getReceptorsManager().setId(this, id);
        this.id = id;
    }

    @Override
    public @Range(from = 0, to = Long.MAX_VALUE) int getIndex() {
        return index;
    }

    @Override
    public void setIndex(@Range(from = 0, to = Long.MAX_VALUE) int index) {
        this.index = index;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    @ApiStatus.Internal
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public @NotNull SqliteDatabase getDatabase() {
        return getTable().getDatabase();
    }

    @Override
    public @NotNull SqliteTable getTable() {
        return table;
    }

    @Override
    public @Nullable <T> T get(@NotNull String id) {
        if (!isLoaded()) {
            throw new IllegalStateException("The receptor isn't loaded.");
        }

        for (ActiveVariableContainer activeVar : getActiveContainers()) {
            if (activeVar instanceof SqlActiveVariableContainer) {
                SqlActiveVariableContainer container = (SqlActiveVariableContainer) activeVar;

                if (container.getVariable() != null) {
                    if (container.getVariable().getId().equals(id)) {
                        //noinspection unchecked
                        return (T) container.get();
                    }
                } else {
                    throw new NullPointerException("The active containers of a receptor needs to have a variable!");
                }
            } else {
                throw new IllegalArgumentException("This receptor contains illegal container types");
            }
        }
        throw new NullPointerException("Couldn't find a variable with id '" + id + "' at the receptor '" + getId() + "'");
    }

    @Override
    public void set(@NotNull String id, @Nullable Object object) {
        if (!isLoaded()) {
            throw new IllegalStateException("The receptor isn't loaded.");
        }

        boolean set = false;
        for (ActiveVariableContainer activeVar : getActiveContainers()) {
            if (activeVar instanceof SqlActiveVariableContainer) {
                SqlActiveVariableContainer container = (SqlActiveVariableContainer) activeVar;

                if (container.getVariable() != null) {
                    if (container.getVariable().getId().equals(id)) {
                        container.set(object);
                        set = true;
                    }
                } else {
                    throw new NullPointerException("The active containers of a receptor needs to have a variable!");
                }
            } else {
                throw new IllegalArgumentException("This receptor contains illegal container types");
            }
        }

        if (!set) {
            throw new NullPointerException("Couldn't find a variable with id '" + id + "' at the receptor '" + getId() + "'");
        }
    }
}
