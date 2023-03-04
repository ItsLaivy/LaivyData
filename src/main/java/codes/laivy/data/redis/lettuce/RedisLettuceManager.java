package codes.laivy.data.redis.lettuce;

import codes.laivy.data.redis.manager.RedisManager;

public interface RedisLettuceManager<R extends RedisLettuceReceptor, V extends RedisLettuceVariable, D extends RedisLettuceDatabase, T extends RedisLettuceTable> extends RedisManager<R, V, D, T> {
}
