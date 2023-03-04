package codes.laivy.data.redis.lettuce.natives.manager;

import codes.laivy.data.redis.lettuce.*;
import codes.laivy.data.redis.lettuce.connection.RedisLettuceConnection;
import codes.laivy.data.redis.lettuce.natives.RedisLettuceConnectionNative;
import codes.laivy.data.redis.manager.RedisReceptorsManager;
import codes.laivy.data.redis.manager.RedisTablesManager;
import codes.laivy.data.redis.manager.RedisVariablesManager;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public class RedisLettuceManagerNative implements RedisLettuceManager<RedisLettuceReceptor, RedisLettuceVariable, RedisLettuceDatabase, RedisLettuceTable> {

    protected @NotNull RedisReceptorsManager<RedisLettuceReceptor> receptorsManager;
    protected @NotNull RedisTablesManager<RedisLettuceTable> tablesManager;
    protected @NotNull RedisVariablesManager<RedisLettuceVariable> variablesManager;

    private final @NotNull RedisLettuceConnectionNative connection;

    public RedisLettuceManagerNative(@NotNull RedisLettuceConnectionNative connection) {
        this.connection = connection;

        this.receptorsManager = new RedisLettuceReceptorsManagerNative();
        this.tablesManager = new RedisLettuceTablesManagerNative();
        this.variablesManager = new RedisLettuceVariablesManagerNative();
    }

    public @NotNull RedisLettuceConnection getConnection() {
        return connection;
    }

    @Override
    public @NotNull RedisLettuceReceptor[] getStored(@NotNull RedisLettuceDatabase database) {
        return new RedisLettuceReceptor[0];
    }

    @Override
    public void load(@NotNull RedisLettuceDatabase database) {
    }

    @Override
    public void unload(@NotNull RedisLettuceDatabase database) {
        for (String key : database.getKeys()) {
            connection.getSync().del(key);
        }
    }

    @Override
    public void delete(@NotNull RedisLettuceDatabase database) {
        unload(database);
    }

    @Override
    public boolean isLoaded(@NotNull RedisLettuceDatabase database) {
        return database.isLoaded();
    }

    @Override
    public @NotNull RedisVariablesManager<RedisLettuceVariable> getVariablesManager() {
        return variablesManager;
    }

    @Override
    public @NotNull RedisReceptorsManager<RedisLettuceReceptor> getReceptorsManager() {
        return receptorsManager;
    }

    @Override
    public @NotNull RedisTablesManager<RedisLettuceTable> getTablesManager() {
        return tablesManager;
    }

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    public @NotNull String getName() {
        return "LaivyData:native-lettuce";
    }
}
