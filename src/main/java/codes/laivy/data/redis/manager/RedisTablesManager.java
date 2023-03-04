package codes.laivy.data.redis.manager;

import codes.laivy.data.api.manager.TablesManager;
import codes.laivy.data.redis.RedisTable;

public interface RedisTablesManager<T extends RedisTable> extends TablesManager<T> {
}
