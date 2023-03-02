package codes.laivy.data.api.manager;

import org.jetbrains.annotations.NotNull;

/**
 * The main manager
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface Manager<T> {

    /**
     * Loads the manager's resources
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void load(@NotNull T object);

    /**
     * Unloads the manager's resources
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void unload(@NotNull T object);

    /**
     * Deletes the manager's resources
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void delete(@NotNull T object);

    /**
     * Checks if the manager is loaded
     * @return true if the manager is loaded
     */
    boolean isLoaded(@NotNull T object);

}
