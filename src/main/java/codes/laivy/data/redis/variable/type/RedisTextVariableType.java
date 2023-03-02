package codes.laivy.data.redis.variable.type;

import codes.laivy.data.redis.variable.RedisVariableType;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     This is a native redis type that converts the objects into string using the {@link Object#toString()} method. It's pretty simple and useful. This variable type has support for others languages LaivyData.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 */
public class RedisTextVariableType implements RedisVariableType {

    @Override
    public @NotNull String serialize(@NotNull Object object) {
        return null;
    }

    @Override
    public @NotNull Object deserialize(@NotNull String value) {
        return null;
    }
}
