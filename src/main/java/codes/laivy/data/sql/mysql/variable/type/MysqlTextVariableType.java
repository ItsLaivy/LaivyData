package codes.laivy.data.sql.mysql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.variable.MysqlVariableType;
import codes.laivy.data.sql.variable.type.SqlTextVariableType;
import com.mysql.cj.MysqlType;
import io.netty.util.internal.UnstableApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.sql.SQLType;

public class MysqlTextVariableType implements SqlTextVariableType<MysqlVariable>, MysqlVariableType<MysqlVariable> {

    private final @NotNull Size size;
    protected @NotNull SQLType type;

    public MysqlTextVariableType(@NotNull Size size) {
        this.size = size;

        type = new SQLType() {
            @Override
            public String getName() {
                return size.getName();
            }

            @Override
            public String getVendor() {
                return "codes.laivy.data.sql.natives.mysql.variable.type";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return getSize().getVendorTypeNumber();
            }
        };
    }

    public @NotNull Size getSize() {
        return size;
    }

    @Override
    public @NotNull SQLType getSqlType() {
        return type;
    }

    @Override
    public void configure(@NotNull SqlVariable variable) {
        variable.getDatabase().getManager().getVariablesManager().setType(variable, getSqlType());
    }

    /**
     * The {@link MysqlTextVariableType} saves the objects as TEXT, you can select here what size do you prefer to use.
     * @author Laivy
     * @since 1.0
     */
    public enum Size {
        /**
         * <p>
         *     This size could store up to 255 bytes. It's recommended for variables that will save simple bytes of data.
         *     Has a performance higher than {@link #TEXT}
         * </p>
         *
         * @since 1.0
         */
        TINYTEXT(255L, "TINYTEXT", MysqlType.TINYTEXT.getVendorTypeNumber()),

        /**
         * <p>
         *     This size could store up to 65,535 bytes. It's recommended for normal use.
         *     Has a performance higher than {@link #MEDIUMTEXT}, lower than {@link #TINYTEXT}
         * </p>
         *
         * @since 1.0
         */
        TEXT(65535L, "TEXT", MysqlType.TEXT.getVendorTypeNumber()),

        /**
         * <p>
         *     This size could store up to 16,777,215 bytes. It's recommended for large variables, with a big data storing
         *     Has a performance higher than {@link #TEXT}, lower than {@link #TEXT}
         * </p>
         *
         * @since 1.0
         */
        MEDIUMTEXT(16777215L, "MEDIUMTEXT", MysqlType.MEDIUMTEXT.getVendorTypeNumber()),

        /**
         * <p>
         *     <b>Note:</b> This variable size can cause a huge performance issue, only use that if you are absolutely convinced what you are doing.
         * </p>
         *
         * <p>
         *     This size could store up to 4.294,967,295L bytes. It's recommended for massive variables.
         *     Has a performance lower than {@link #MEDIUMTEXT}
         * </p>
         *
         * @since 1.0
         */
        @UnstableApi
        LONGTEXT(4294967295L, "LONGTEXT", MysqlType.LONGTEXT.getVendorTypeNumber()),
        ;

        private final @Range(from = 1, to = Long.MAX_VALUE) long capacity;
        private final @NotNull String name;
        private final @Range(from = 1, to = Integer.MAX_VALUE) int vendorTypeNumber;

        Size(@Range(from = 1, to = Long.MAX_VALUE) long capacity, @NotNull String name, @Range(from = 1, to = Integer.MAX_VALUE) int vendorTypeNumber) {
            this.capacity = capacity;
            this.name = name;
            this.vendorTypeNumber = vendorTypeNumber;
        }

        public @Range(from = 1, to = Long.MAX_VALUE) long getCapacity() {
            return capacity;
        }

        public @NotNull String getName() {
            return name;
        }

        public @Range(from = 1, to = Integer.MAX_VALUE) int getVendorTypeNumber() {
            return vendorTypeNumber;
        }
    }

}
