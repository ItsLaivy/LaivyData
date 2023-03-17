package codes.laivy.data.redis.variable.type;

import codes.laivy.data.redis.variable.RedisVariableType;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable String serialize(@Nullable Object object) {
        if (object == null) {
            return "%null%";
        }
        return object.toString();
    }

    @Override
    public @Nullable Object deserialize(@Nullable String value) {
        if (value == null) {
            return null;
        } else if (value.equals("%null%")) {
            return null;
        }
        return value;
    }

    @Override
    public boolean isCompatible(@Nullable Object object) {
        return true;
    }
}
