package codes.laivy.data.sql;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.table.Tableable;
import io.netty.util.internal.UnstableApi;
import org.jetbrains.annotations.*;

/**
 * @author Laivy
 * @since 1.0
 */
public interface SqlReceptor extends Receptor, Tableable {

    @Override
    @Contract(pure = true)
    @NotNull SqlDatabase getDatabase();

    @Override
    @Contract(pure = true)
    @NotNull SqlTable getTable();

    @Override
    @NotNull String getId();

    @Override
    void setId(@NotNull String id);

    /**
     * This receptor's index at the table
     * @return the index
     */
    @Range(from = 0, to = Long.MAX_VALUE) int getIndex();

    /**
     * Only use that method if you are absolutely convinced of what are you doing. This will change the natural order of the AUTO_INCREMENT attribute.
     * @param index the new receptor's index
     */
    @UnstableApi
    void setIndex(@Range(from = 0, to = Long.MAX_VALUE) int index);

}
