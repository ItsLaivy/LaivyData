package codes.laivy.data.sql.manager;

import codes.laivy.data.api.manager.ReceptorsManager;
import codes.laivy.data.sql.SqlReceptor;
import org.jetbrains.annotations.NotNull;

public interface SqlReceptorsManager<R extends SqlReceptor> extends ReceptorsManager<R> {

    void setId(@NotNull R receptor, @NotNull String id);

}
