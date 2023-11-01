package codes.laivy.data.mysql;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


/**
 * Represents a version of MySQL.
 *
 * @since 2.0
 */
public final class MysqlVersion {

    private final @NotNull String version;
    private final int major;
    private final int minor;

    /**
     * Constructs a MysqlVersion instance with the specified version, major, and minor version numbers.
     *
     * @param version The full version string
     * @param major   The major version number
     * @param minor   The minor version number
     * @since 2.0
     */
    private MysqlVersion(@NotNull String version, int major, int minor) {
        this.version = version;
        this.major = major;
        this.minor = minor;
    }

    /**
     * Creates an instance of MysqlVersion from the given full version string.
     *
     * @param fullVersion The full version string
     * @return A MysqlVersion instance
     * @throws IllegalStateException If the version string is invalid or exceeds 256 characters
     * @since 2.0
     */
    @Contract(pure = true)
    public static @NotNull MysqlVersion of(@NotNull String fullVersion) {
        if (fullVersion.length() > 256) {
            throw new IllegalStateException("Invalid version size");
        }

        if (!fullVersion.matches("[0-9]+(\\.[0-9]+)?")) {
            try {
                String[] split = fullVersion.split("\\.");
                int major = Integer.parseInt(split[0]);
                int minor = Integer.parseInt(split[1]);

                return new MysqlVersion(fullVersion, major, minor);
            } catch (Throwable throwable) {
                throw new IllegalStateException("Cannot extract major and minor version from '" + fullVersion + "'");
            }
        } else {
            throw new IllegalStateException("This isn't a valid MySQL version '" + fullVersion + "'");
        }
    }

    /**
     * Gets the full version string.
     *
     * @return The full version string
     * @since 2.0
     */
    @Contract(pure = true)
    public @NotNull String getVersion() {
        return version;
    }

    /**
     * Gets the major version number.
     *
     * @return The major version number
     * @since 2.0
     */
    @Contract(pure = true)
    public int getMajor() {
        return major;
    }

    /**
     * Gets the minor version number.
     *
     * @return The minor version number
     * @since 2.0
     */
    @Contract(pure = true)
    public int getMinor() {
        return minor;
    }

    /**
     * Checks if this MysqlVersion is equal to another object.
     *
     * @param object The object to compare
     * @return True if equal, false otherwise
     * @since 2.0
     */
    @Override
    @Contract(pure = true)
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlVersion)) return false;
        MysqlVersion that = (MysqlVersion) object;
        return Objects.equals(getVersion(), that.getVersion());
    }

    /**
     * Generates the hash code for this MysqlVersion.
     *
     * @return The hash code
     * @since 2.0
     */
    @Override
    @Contract(pure = true)
    public int hashCode() {
        return Objects.hash(getVersion());
    }

    /**
     * Returns a string representation of this MysqlVersion.
     *
     * @return The string representation
     * @since 2.0
     */
    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        return version;
    }
}
