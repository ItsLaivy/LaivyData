package codes.laivy.data.sql;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.table.Tableable;
import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import io.netty.util.internal.UnstableApi;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.regex.Pattern.matches;

public class SqlReceptor implements Receptor, Tableable {

    /**
     * The default regex for SQL receptor names
     */
    private static final @NotNull @Language("RegExp") String ID_REGEX = "^.{0,127}";

    private final @NotNull SqlTable table;
    protected @NotNull @Pattern(ID_REGEX) @Subst(ID_REGEX) String id;

    protected boolean loaded;
    private boolean isNew;

    private @Range(from = 0, to = Integer.MAX_VALUE) int index;

    private final @NotNull Set<@NotNull InactiveVariableContainer> inactiveContainers = new LinkedHashSet<>();
    private final @NotNull Set<@NotNull ActiveVariableContainer> activeContainers = new LinkedHashSet<>();

    public SqlReceptor(@NotNull SqlTable table, @Subst(ID_REGEX) @NotNull String id) {
        this.table = table;
        this.id = id;

        if (!matches(ID_REGEX, id)) {
            throw new IllegalArgumentException("The receptor id must follow the regex '" + ID_REGEX + "'");
        }

        loaded = false;
        isNew = false;
    }

    @Override
    @Contract(pure = true)
    public @NotNull SqlDatabase getDatabase() {
        return getTable().getDatabase();
    }

    @Override
    @Contract(pure = true)
    public @NotNull SqlTable getTable() {
        return table;
    }

    @Override
    @Pattern(ID_REGEX)
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull @Pattern(ID_REGEX) @Subst(ID_REGEX) String id) {
        this.id = id;
    }

    /**
     * This receptor's index at the table
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Only use that method if you are absolutely convinced of what are you doing. This will change the natural order of the AUTO_INCREMENT attribute.
     * @param index the new receptor's index
     */
    @UnstableApi
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void load() {
        getDatabase().getManager().getReceptorsManager().load(this);
        getTable().receptors.add(this);
        loaded = true;
    }

    @Override
    public void unload(boolean save) {
        getDatabase().getManager().getReceptorsManager().unload(this, save);
        getTable().receptors.remove(this);
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
    public boolean isNew() {
        return isNew;
    }

    /**
     * Use at your own risk, it's made for internal use.
     * @param isNew the new state of receptor
     */
    @ApiStatus.Internal
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public <T> @Nullable T get(@NotNull @Pattern(ID_REGEX) @Subst(ID_REGEX) String name) {
        return null;
    }

    @Override
    public void set(@NotNull @Pattern(ID_REGEX) @Subst(ID_REGEX) String name, @Nullable Object object) {

    }

    public @NotNull Set<InactiveVariableContainer> getInactiveContainers() {
        return inactiveContainers;
    }

    public @NotNull Set<ActiveVariableContainer> getActiveContainers() {
        return activeContainers;
    }

}
