package codes.laivy.data.sql;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.table.Tableable;
import io.netty.util.internal.UnstableApi;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.*;

public interface SqlReceptor extends Receptor, Tableable {

    @Override
    @Contract(pure = true)
    @NotNull SqlDatabase getDatabase();

    @Override
    @Contract(pure = true)
    @NotNull SqlTable getTable();

    @Override
    @Pattern(".*")
    @NotNull String getId();

    @Override
    void setId(@NotNull @Pattern(".*") @Subst("receptor id") String id);

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
