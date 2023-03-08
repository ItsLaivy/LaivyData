package codes.laivy.data.redis.variable;

import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RedisKey {

    /**
     * @return The redis key at the database
     */
    @NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String getKey();

    /**
     * @return The redis value at the database
     */
    default @Nullable String getValue() {
        return null;
    }
}
