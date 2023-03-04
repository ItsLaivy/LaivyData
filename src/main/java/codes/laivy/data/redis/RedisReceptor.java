package codes.laivy.data.redis;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface RedisReceptor extends Receptor {
    @Override
    @NotNull RedisDatabase getDatabase();

    /**
     * <p>
     *     This is the main storing system. As default, redis couldn't store data, after the receptor gets
     *     unloaded, it will delete all data, if is set, all the data will be
     *     saved into this active variable container.
     * </p><br>
     * <p>
     *     Will load the data of the container too.
     * </p>
     *
     * @return the storing container
     */
    default @Nullable ActiveVariableContainer getStoringData() {
        return null;
    }

    @Contract(pure = true)
    @Nullable RedisTable getTable();

    /**
     * Returns an array with the variables that this receptor can use, it gets the variables from table if exists, database otherwise.
     * @return The arrays that could hold this receptor
     */
    default @NotNull RedisVariable[] getVariables() {

    }

    /**
     * Gets a full list of all registered keys by this receptor
     * @return the set containing all keys of this receptor
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull Set<String> getKeys();

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    @NotNull String getId();

    @Override
    void setId(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id);
}
