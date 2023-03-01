package codes.laivy.data.sql.manager;

import codes.laivy.data.api.manager.VariablesManager;
import codes.laivy.data.sql.SqlVariable;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLType;

public interface SqlVariablesManager<V extends SqlVariable> extends VariablesManager<V> {

    /**
     * Sets the variable type
     * @param type the new variable type
     */
    void setType(@NotNull V variable, @NotNull SQLType type);

}
