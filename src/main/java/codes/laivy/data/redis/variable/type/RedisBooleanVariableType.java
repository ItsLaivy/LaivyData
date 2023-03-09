package codes.laivy.data.redis.variable.type;

import org.jetbrains.annotations.Nullable;

public class RedisBooleanVariableType extends RedisTextVariableType {
    @Override
    public @Nullable Object deserialize(@Nullable String value) {
        if (value == null) {
            return null;
        }

        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            throw new IllegalArgumentException("This object doesn't seems to be a boolean object!");
        }
    }

    @Override
    public boolean isCompatible(@Nullable Object object) {
        return object == null || object instanceof Boolean;
    }
}
