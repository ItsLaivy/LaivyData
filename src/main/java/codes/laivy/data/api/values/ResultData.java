package codes.laivy.data.api.values;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The {@link ResultData} is the main data flow controller, every database operation that returns something will need to have their DataResult.
 *
 * @author Laivy
 * @since 1.0
 */
public interface ResultData extends Iterator<Map<String, Object>> {

    /**
     * Sets the cursor to the first result
     *
     * @author Laivy
     * @since 1.0
     */
    void first();

    /**
     * Gets the current cursor index
     * @return the current cursor index
     *
     * @author Laivy
     * @since 1.0
     */
    int index();

    @Unmodifiable
    @NotNull Set<@NotNull Map<@NotNull String, @Nullable Object>> getValues();

}
