package codes.laivy.data.redis.variable;

import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The redis key that contains the key and the value at the database
 * @author Laivy
 * @since 1.0
 */
public interface RedisKey {

    /**
     * @return The redis key at the database
     */
    @NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String getKey();

    /**
     * @return The redis value at the database
     */
    default @Nullable String getValue() {
        throw new UnsupportedOperationException("This redis key's value isn't supported.");
    }
}
