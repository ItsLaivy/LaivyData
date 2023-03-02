package codes.laivy.data.redis;

import codes.laivy.data.api.table.Table;
import org.jetbrains.annotations.NotNull;

public interface RedisTable extends Table {
    @Override
    @NotNull RedisDatabase getDatabase();
}
