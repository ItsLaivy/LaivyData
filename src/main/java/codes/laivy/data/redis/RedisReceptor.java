package codes.laivy.data.redis;

import codes.laivy.data.api.receptor.Receptor;
import org.jetbrains.annotations.NotNull;

public interface RedisReceptor extends Receptor {
    @Override
    @NotNull RedisDatabase getDatabase();
}
