package codes.laivy.data.redis.lettuce.natives.manager;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.lettuce.RedisLettuceVariable;
import codes.laivy.data.redis.manager.RedisVariablesManager;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainer;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainerImpl;
import codes.laivy.data.redis.variable.container.RedisInactiveVariableContainerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Laivy
 * @since 1.0
 */
public class RedisLettuceVariablesManagerNative implements RedisVariablesManager<RedisLettuceVariable> {
    @Override
    public void load(@NotNull RedisLettuceVariable variable) {
        for (RedisReceptor receptor : variable.getDatabase().getLoadedReceptors()) {
            for (InactiveVariableContainer inactiveContainer : new LinkedList<>(receptor.getInactiveContainers())) {
                if (inactiveContainer.getVariable().equals(receptor.getKey(variable))) {
                    receptor.getInactiveContainers().remove(inactiveContainer);
                    receptor.getActiveContainers().add(new RedisActiveVariableContainerImpl(variable, receptor, inactiveContainer.get()));
                }
            }
        }
    }

    @Override
    public void unload(@NotNull RedisLettuceVariable variable) {
        for (RedisReceptor receptor : variable.getDatabase().getLoadedReceptors()) {
            for (ActiveVariableContainer activeContainer : new LinkedList<>(receptor.getActiveContainers())) {
                if (activeContainer instanceof RedisActiveVariableContainer) {
                    RedisActiveVariableContainer redisContainer = (RedisActiveVariableContainer) activeContainer;

                    if (Objects.equals(redisContainer.getVariable(), variable)) {
                        receptor.getActiveContainers().remove(activeContainer);
                        receptor.getInactiveContainers().add(new RedisInactiveVariableContainerImpl(receptor.getKey(variable), receptor, activeContainer.get()));
                    }
                }
            }
        }
    }

    @Override
    public boolean isLoaded(@NotNull RedisLettuceVariable variable) {
        return variable.isLoaded();
    }

    @Override
    public void delete(@NotNull RedisLettuceVariable variable) {

    }
}
