package codes.laivy.data.redis.variable;

import codes.laivy.data.api.variable.VariableType;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     The redis variable type has only two methods, the {@link #serialize(Object)} that converts the object into a string (to save it on the redis) and the {@link #deserialize(String)} that converts that string again into an object.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 */
public interface RedisVariableType extends VariableType {

    /**
     * Serializes a object into a string to be able to store it into the redis database.
     * @param object the object that will be converted into string
     * @return the string to store into redis
     *
     * @author Laivy
     * @since 1.0
     */
    @Nullable String serialize(@Nullable Object object);

    /**
     * Deserializes a key from the Redis database and converts it into the object again
     * @param value the key value
     * @return the object instance
     *
     * @author Laivy
     * @since 1.0
     */
    @Nullable Object deserialize(@Nullable String value);

}
