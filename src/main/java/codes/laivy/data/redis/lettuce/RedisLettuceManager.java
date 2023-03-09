package codes.laivy.data.redis.lettuce;

import codes.laivy.data.redis.manager.RedisManager;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisLettuceManager<R extends RedisLettuceReceptor, V extends RedisLettuceVariable, D extends RedisLettuceDatabase, T extends RedisLettuceTable> extends RedisManager<R, V, D, T> {
}
