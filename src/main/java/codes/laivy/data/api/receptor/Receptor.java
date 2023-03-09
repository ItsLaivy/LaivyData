package codes.laivy.data.api.receptor;

import codes.laivy.data.api.database.Database;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.api.variable.VariableType;
import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Receptor {

    /**
     * Gets the database's receptor
     * @return the database of this receptor
     *
     * @author Laivy
     * @since 1.0
     */
    @NotNull Database getDatabase();

    /**
     * The id is used to get the receptor at the database, some databases uses custom regexes.
     * @return the receptor id
     *
     * @author Laivy
     * @since 1.0
     */
    @Pattern(".*")
    @NotNull String getId();

    /**
     * This will change the receptor's database id.
     * @param id the new receptor id
     *
     * @author Laivy
     * @since 1.0
     */
    void setId(@NotNull @Pattern(".*") String id);

    /**
     * Loads the receptor from the database and gets all the data
     * A receptor instance couldn't be loaded if a different instance of the same id at this database is already loaded
     *
     * @author Laivy
     * @since 1.0
     */
    void load();

    /**
     * Save receptor variables and values into the database
     *
     * @author Laivy
     * @since 1.0
     */
    void save();

    /**
     * Unloads the receptor from the database and removes all data from the memory.
     *
     * @author Laivy
     * @since 1.0
     */
    void unload(boolean save);

    /**
     * Checks if a receptor is loaded
     * @return true if is loaded
     *
     * @author Laivy
     * @since 1.0
     */
    boolean isLoaded();

    /**
     * Unloads the receptor and deletes from the database.
     *
     * @author Laivy
     * @since 1.0
     */
    void delete();

    /**
     * A receptor is new if it has created before loaded at the database.
     * @return true if the receiver didn't exist before loading and been created
     *
     * @author Laivy
     * @since 1.0
     */
    boolean isNew();

    /**
     * This sets the new state of a receptor
     * @param isNew the new state of receptor
     *
     * @author Laivy
     * @since 1.0
     */
    void setNew(boolean isNew);

    /**
     * Unloads the receptor and loads again if the receptor is loaded.
     * @param save save or not if the reloading process occurs
     *
     * @author Laivy
     * @since 1.0
     */
    default void reload(boolean save) {
        if (isLoaded()) {
            unload(save);
            load();
        }
    }

    /**
     * Gets a value of a {@link Variable} according to the {@link VariableType} processor
     * @param id the {@link Variable} id
     * @return the object
     *
     * @author Laivy
     * @since 1.0
     */
    @Nullable <T> T get(@NotNull @Pattern(".*") @Subst("variable_id") String id);

    /**
     * Sets a value of a {@link Variable} according to the {@link VariableType} processor
     * @param id the {@link Variable} id
     * @param object the object
     *
     * @author Laivy
     * @since 1.0
     */
    void set(@NotNull @Pattern(".*") @Subst("variable_id") String id, @Nullable Object object);

    /**
     * The unloaded variable containers
     * @return a set containing all unloaded variable containers
     *
     * @author Laivy
     * @since 1.0
     */
    @NotNull Set<@NotNull InactiveVariableContainer> getInactiveContainers();

    /**
     * The loaded variable containers
     * @return a set containing all loaded variable containers
     *
     * @author Laivy
     * @since 1.0
     */
    @NotNull Set<@NotNull ActiveVariableContainer> getActiveContainers();

}
