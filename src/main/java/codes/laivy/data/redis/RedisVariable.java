package codes.laivy.data.redis;

import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.redis.variable.RedisVariableType;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

public interface RedisVariable extends Variable {
    @Override
    @NotNull RedisDatabase getDatabase();

    @Override
    @NotNull RedisVariableType getType();

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    @NotNull String getId();

    @Override
    void setId(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_name") String id);
}
