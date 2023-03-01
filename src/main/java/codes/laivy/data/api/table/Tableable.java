package codes.laivy.data.api.table;

import org.jetbrains.annotations.NotNull;

/**
 * Indicates the object supports tables
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface Tableable {

    @NotNull Table getTable();

}
