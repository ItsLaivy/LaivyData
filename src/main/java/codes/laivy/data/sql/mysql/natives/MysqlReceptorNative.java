package codes.laivy.data.sql.mysql.natives;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.regex.Pattern.matches;

/**
 * <p>
 *     The native MySQL Receptor of LaivyData.
 *     This native receptor doesn't autoload, you need to load yourself.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class MysqlReceptorNative implements MysqlReceptor {

    private final @NotNull MysqlTable table;
    private @NotNull @Pattern("^.{0,128}$") @Subst("receptor id") String id;

    private @Range(from = 0, to = Long.MAX_VALUE) int index;

    protected boolean loaded = false;
    protected boolean isNew = false;

    private final @NotNull Set<InactiveVariableContainer> inactiveVariableContainers = new LinkedHashSet<>();
    private final @NotNull Set<ActiveVariableContainer> activeVariableContainers = new LinkedHashSet<>();

    public MysqlReceptorNative(@NotNull MysqlTable table, @NotNull @Pattern("^.{0,128}$") @Subst("receptor id") String id) {
        this.table = table;
        this.id = id;

        if (!matches("^.{0,128}$", id)) {
            throw new IllegalArgumentException("The receptor id must follow the regex '^.{0,128}$'");
        }
    }

    @Override
    public void load() {
        getDatabase().getManager().getReceptorsManager().load(this);
        getTable().getLoadedReceptors().add(this);
        loaded = true;
    }

    @Override
    public void save() {
        getDatabase().getManager().getReceptorsManager().save(this);
    }

    @Override
    public void unload(boolean save) {
        getDatabase().getManager().getReceptorsManager().unload(this, save);
        getTable().getLoadedReceptors().remove(this);
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void delete() {
        unload(false);
        getDatabase().getManager().getReceptorsManager().delete(this);
    }

    @Override
    public @NotNull Set<@NotNull InactiveVariableContainer> getInactiveContainers() {
        return inactiveVariableContainers;
    }

    @Override
    public @NotNull Set<@NotNull ActiveVariableContainer> getActiveContainers() {
        return activeVariableContainers;
    }

    @Override
    public @NotNull MysqlTable getTable() {
        return table;
    }

    @Override
    @Pattern("^.{0,128}$")
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull @Pattern("^.{0,128}$") @Subst("receptor id") String id) {
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

    /**
     * Use at your own risk, it's made for internal use.
     * @param isNew the new state of receptor
     *
     * @author Laivy
     * @since 1.0
     */
    @ApiStatus.Internal
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public <T> @Nullable T get(@NotNull String name) {
        return null;
    }

    @Override
    public void set(@NotNull String name, @Nullable Object object) {

    }

    @Override
    public @NotNull MysqlDatabase getDatabase() {
        return getTable().getDatabase();
    }
}
