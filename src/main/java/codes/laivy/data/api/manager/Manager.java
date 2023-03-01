package codes.laivy.data.api.manager;

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
    void load(T object);

    /**
     * Unloads the manager's resources
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void unload(T object);

    /**
     * Deletes the manager's resources
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void delete(T object);

    /**
     * Checks if the manager is loaded
     * @return true if the manager is loaded
     */
    boolean isLoaded(T object);

}
