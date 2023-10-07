package codes.laivy.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public final class MysqlVersion {

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
                throw new IllegalStateException("Cannot get major and minor version from '" + fullVersion + "'");
            }
        } else {
            throw new IllegalStateException("This isn't a valid mysql version '" + fullVersion + "'");
        }
    }

    private final @NotNull String version;
    private final int major;
    private final int minor;

    private MysqlVersion(@NotNull String version, int major, int minor) {
        this.version = version;
        this.major = major;
        this.minor = minor;
    }

    @Contract(pure = true)
    public @NotNull String getVersion() {
        return version;
    }

    @Contract(pure = true)
    public int getMajor() {
        return major;
    }

    @Contract(pure = true)
    public int getMinor() {
        return minor;
    }

    @Override
    @Contract(pure = true)
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlVersion)) return false;
        MysqlVersion that = (MysqlVersion) object;
        return Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return Objects.hash(getVersion());
    }

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        return version;
    }
}
