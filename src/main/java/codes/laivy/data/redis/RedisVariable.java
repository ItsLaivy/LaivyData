package codes.laivy.data.redis;

import codes.laivy.data.api.database.Database;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.api.variable.VariableType;
import org.jetbrains.annotations.NotNull;

public interface RedisVariable extends Variable {
    @Override
    @NotNull RedisDatabase getDatabase();

    @Override
    @NotNull VariableType getType();
}
