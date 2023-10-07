package codes.laivy.data.data;

import codes.laivy.data.Database;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Represents an abstract receptor entity, extending Data, with common receptor-related operations.
 * Implementations must define specific behavior for receptor id validation.
 *
 * @author Laivy
 * @since 1.0
 */
public abstract class Receptor extends Data {

    private final @NotNull String id;

    /**
     * Constructs a Receptor instance with the specified id and associated database.
     *
     * @param id       The unique id of the receptor
     * @param database The database instance
     * @throws UnsupportedOperationException If the id does not match the pattern
     *
     * @author Laivy
     * @since 1.0
     */
    protected Receptor(@NotNull String id, @NotNull Database database) {
        super(database);
        this.id = id;

        if (!getPattern().matcher(id).matches()) {
            throw new UnsupportedOperationException("The receptor id '" + id + "' doesn't match the regex '" + getPattern().pattern() + "'");
        }
    }

    /**
     * Gets the main pattern used for validating receptor ids.
     *
     * @return The pattern for id validation
     * @since 2.0
     */
    @Contract(pure = true)
    protected abstract @NotNull Pattern getPattern();

    /**
     * Gets the unique id of the receptor.
     *
     * @return The unique id of the receptor
     * @since 1.0
     */
    @Contract(pure = true)
    public final @NotNull String getId() {
        return this.id;
    }

}
