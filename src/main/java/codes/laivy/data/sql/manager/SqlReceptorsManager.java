package codes.laivy.data.sql.manager;

import codes.laivy.data.api.manager.ReceptorsManager;
import codes.laivy.data.sql.SqlReceptor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqlReceptorsManager<R extends SqlReceptor> extends ReceptorsManager<R> {

    void setId(@NotNull R receptor, @NotNull String id);

    void save(@NotNull R[] receptors);
    void unload(@NotNull R[] receptors, boolean save);

}
