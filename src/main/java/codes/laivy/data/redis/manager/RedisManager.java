package codes.laivy.data.redis.manager;

import codes.laivy.data.api.manager.DatabaseManager;
import codes.laivy.data.redis.RedisDatabase;
import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.RedisTable;
import codes.laivy.data.redis.RedisVariable;

public interface RedisManager<R extends RedisReceptor, V extends RedisVariable, D extends RedisDatabase, T extends RedisTable> extends DatabaseManager<R, V, D, T> {

}
