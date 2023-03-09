package codes.laivy.data.api.manager;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.values.ResultData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link ReceptorsManager} controls the receptors
 * @param <R> the receptor type of this manager
 *
 * @author Laivy
 * @since 1.0
 */
public interface ReceptorsManager<R extends Receptor> extends Manager<R> {

    /**
     * Gets the receptor data at the database
     * @param receptor the receptor
     * @return the result data containing necessary informations to load that receptor
     *
     * @author Laivy
     * @since 1.0
     */
    @Nullable ResultData getData(@NotNull R receptor);

    /**
     * Unloads the receptor and save (or not) its data to database.
     * @param receptor the receptor
     *
     * @author Laivy
     * @since 1.0
     */
    void unload(@NotNull R receptor, boolean save);

    /**
     * Saves a receptor into the database
     * @param receptor the receptor
     *
     * @author Laivy
     * @since 1.0
     */
    void save(@NotNull R receptor);

    /**
     * Deletes a receptor from the database
     * @param receptor the receptor
     *
     * @author Laivy
     * @since 1.0
     */
    void delete(@NotNull R receptor);

}
