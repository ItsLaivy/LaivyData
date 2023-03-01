package codes.laivy.data.api.variable;

import codes.laivy.data.api.database.Database;
import org.jetbrains.annotations.NotNull;

/**
 * The VariableType controls, sets, gets, removes, and modifies the value of a variable. This is important.
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface VariableType {

    @NotNull Database getDatabase();

}
