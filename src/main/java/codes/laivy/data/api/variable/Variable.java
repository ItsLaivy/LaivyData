package codes.laivy.data.api.variable;

import codes.laivy.data.api.database.Database;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The variable will not be responsible for storing values directly; it will be responsible for creating the environment for them, acting as a container for the values, similar to a data storage engine.
 * @author ItsLaivy
 * @since 1.0
 */
public interface Variable {

    /**
     * This will return the variable's default value, it could be anything :)
     * @return the variable's default value
     */
    @Nullable Object getDefault();

    /**
     * The id of variables must follow the database regexes, be aware. Not all characters will be allowed here.
     * @return the variable id
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @Pattern(".*")
    @NotNull String getId();

    /**
     * This will change the variable's database id.
     * @param id the new variable id
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void setId(@NotNull @Pattern(".*") String id);

    /**
     * The database of the variable, every variable must have one.
     * @return the database
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull Database getDatabase();

    /**
     * The {@link VariableType} is responsible for controlling the value of this variable, which will be managed by the value manager.
     * @return this variable's type controller
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull VariableType getType();

    /**
     * Loads the variable from the database and let it ready :)
     * You cannot load if a variable is loaded at this database with this same id.
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void load();

    /**
     * Unloads the variable and all its components at the database
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void unload();

    /**
     * Deletes the variable from the database
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void delete();

    /**
     * Checks if this variable is loaded or not
     * @return true if the variable is loaded
     */
    boolean isLoaded();

}
