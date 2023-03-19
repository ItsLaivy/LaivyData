package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.redis.lettuce.RedisLettuceResultData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class RedisLettuceResultDataNative implements RedisLettuceResultData {

    private final @NotNull Map<@NotNull String, @Nullable Object>[] array;
    private @Range(from = 0, to = Integer.MAX_VALUE) int index;

    public RedisLettuceResultDataNative(@NotNull Set<@NotNull Map<@NotNull String, @Nullable Object>> content) {
        //noinspection unchecked
        this.array = content.toArray(new Map[0]);
    }

    @Override
    public boolean hasNext() {
        return array.length > (index + 1);
    }

    @Override
    public Map<String, Object> next() {
        Map<String, Object> map = array[index];
        index++;
        return map;
    }

    @Override
    public void first() {
        index = 0;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    @Unmodifiable
    public @NotNull Set<@NotNull Map<@NotNull String, @Nullable Object>> getValues() {
        return new LinkedHashSet<>(Arrays.asList(array));
    }

}
