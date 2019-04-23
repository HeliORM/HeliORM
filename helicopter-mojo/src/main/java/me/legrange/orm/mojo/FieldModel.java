package me.legrange.orm.mojo;

import java.util.Date;

/**
 *
 * @author gideon
 */
public interface FieldModel {

    public enum Type {
        STRING, LONG, INTEGER, DOUBLE, BYTE, FLOAT, SHORT, BOOLEAN, DATE("java.util.Date"), UNSUPPORTED;

        private final String javaType;

        private Type() {
            this.javaType = ("" + name().charAt(0)).toUpperCase() + name().substring(1).toLowerCase();
        }

        private Type(String javaType) {
            this.javaType = javaType;
        }

        public String javaType() {
            return javaType;
        }

        public static Type typeFor(Class<?> type) {
            if (type.isPrimitive()) {
                if (type == Byte.TYPE) {
                    return BYTE;
                }
                if (type == Short.TYPE) {
                    return SHORT;
                }
                if (type == Integer.TYPE) {
                    return INTEGER;
                }
                if (type == Long.TYPE) {
                    return LONG;
                }
                if (type == Float.TYPE) {
                    return FLOAT;
                }
                if (type == Double.TYPE) {
                    return DOUBLE;
                }
                if (type == Boolean.TYPE) {
                    return BOOLEAN;
                }
                return Type.UNSUPPORTED;
            }
            if (Number.class.isAssignableFrom(type)) {
                if (Byte.class.isAssignableFrom(type)) {
                    return Type.BYTE;
                }
                if (Short.class.isAssignableFrom(type)) {
                    return Type.SHORT;
                }
                if (Integer.class.isAssignableFrom(type)) {
                    return Type.INTEGER;
                }
                if (Long.class.isAssignableFrom(type)) {
                    return Type.LONG;
                }
                if (Float.class.isAssignableFrom(type)) {
                    return Type.FLOAT;
                }
                if (Double.class.isAssignableFrom(type)) {
                    return Type.DOUBLE;
                }
                return Type.UNSUPPORTED;
            }
            if (Boolean.class.isAssignableFrom(type)) {
                return Type.BOOLEAN;
            }
            if (String.class.isAssignableFrom(type)) {
                return Type.STRING;
            }
            if (Date.class.isAssignableFrom(type)) {
                return Type.DATE;
            }
            return Type.UNSUPPORTED;
        }

    }

    String getSqlName();

    String getJavaName();

    Type getType();

}
