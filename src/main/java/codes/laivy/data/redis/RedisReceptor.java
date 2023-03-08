package codes.laivy.data.redis;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.redis.variable.RedisKey;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

public interface RedisReceptor extends Receptor {
    @Override
    @NotNull RedisDatabase getDatabase();

    /**
     * Returns a string key of a variable at this receptor
     * @return the string key of the variable
     */
    @NotNull String getKey(@NotNull RedisVariable variable);

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
        Set<RedisVariable> variables = new LinkedHashSet<>();
        if (getTable() != null) {
            variables.addAll(getTable().getLoadedVariables());
        } else {
            for (RedisVariable variable : getDatabase().getLoadedVariables()) {
                if (variable.getTable() == null) {
                    variables.add(variable);
                }
            }
        }
        return variables.toArray(new RedisVariable[0]);
    }

    /**
     * Gets a full list of all registered keys by this receptor
     * @return the set containing all keys of this receptor
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull Set<RedisKey> getKeys();

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    @NotNull String getId();

    @Override
    void setId(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id);
}
