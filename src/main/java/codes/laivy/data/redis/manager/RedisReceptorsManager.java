package codes.laivy.data.redis.manager;

import codes.laivy.data.api.manager.ReceptorsManager;
import codes.laivy.data.redis.RedisReceptor;

/**
 * A Redis receptor manager needs to consider the {@link RedisReceptor#getStoringData()} too!
 * @param <R> The receptor type
 */
public interface RedisReceptorsManager<R extends RedisReceptor> extends ReceptorsManager<R> {
}
