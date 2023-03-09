package codes.laivy.data.api.table;

import org.jetbrains.annotations.NotNull;

/**
 * Indicates the object supports tables
 *
 * @author Laivy
 * @since 1.0
 */
public interface Tableable {

    /**
     * @return the table
     * @author Laivy
     * @since 1.0
     */
    @NotNull Table getTable();

}
