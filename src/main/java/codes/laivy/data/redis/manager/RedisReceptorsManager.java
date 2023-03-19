package codes.laivy.data.redis.manager;

import codes.laivy.data.api.manager.ReceptorsManager;
import codes.laivy.data.api.values.ResultData;
import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.values.RedisResultData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Redis receptor manager needs to consider the {@link RedisReceptor#getStoringData()} too!
 * @param <R> The receptor type
 */
public interface RedisReceptorsManager<R extends RedisReceptor> extends ReceptorsManager<R> {

    @Override
    @Nullable RedisResultData getData(@NotNull R receptor);
}
