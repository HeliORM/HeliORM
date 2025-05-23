package com.heliorm.sql;

import com.heliorm.Field;
import com.heliorm.OrmException;
import com.heliorm.Table;
import com.heliorm.UncaughtOrmException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Function;

import static java.lang.String.format;

/**
 * A helper class that provides common functionality for unpacking POJOs from SQL result sets.
 *
 * @author gideon
 */
class ResultSetHelper {

    private final PojoOperations pops;
    private final Function<Field<?, ?>, String> getFieldId;

    ResultSetHelper(PojoOperations pops, Function<Field<?, ?>, String> getFieldId) {
        this.pops = pops;
        this.getFieldId = getFieldId;
    }

    /**
     * Creates a Pojo for the given table from the element currently at the
     * result set cursor.
     *
     * @param <O>   The type of Pojo
     * @param rs    The result set
     * @param table The table
     * @return The pojo
     * @throws OrmException Thrown if there is an error building the Pojo.
     */
    <O> O makeObjectFromResultSet(ResultSet rs, Table<O> table) throws OrmException {
        return !table.isRecord() ? makePojoFromResultSet(rs, table) : makeRecordFromResultSet(rs, table);
    }

    /**
     * Creates a Pojo for the given table from the element currently at the
     * result set cursor.
     *
     * @param <O>   The type of Pojo
     * @param rs    The result set
     * @param table The table
     * @return The pojo
     * @throws OrmException Thrown if there is an error building the Pojo.
     */
    private <O> O makePojoFromResultSet(ResultSet rs, Table<O> table) throws OrmException {
        try {
            O pojo = pops.newPojoInstance(table);
            for (var field : table.getFields()) {
                setValueInPojo(pojo, field, rs);
            }
            return pojo;
        } catch (OrmException ex) {
            throw new OrmException(format("Error reading table %s (%s)", table.getSqlTable(), ex.getMessage()), ex);
        }
    }

    private <O> O makeRecordFromResultSet(ResultSet rs, Table<O> table) throws OrmException {
        var cons = findCanononicalConstructor(table);
        try {
            return cons.newInstance(table.getFields().stream()
                    .map(field -> getValue(rs, field))
                    .toArray());
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new OrmException(e.getMessage(), e);
        }
    }

    private <O> void setValueInPojo(O pojo, Field<O, ?> field, ResultSet rs) throws OrmException {
        String column = getFieldId(field);
        switch (field.getFieldType()) {
            case LONG:
            case INTEGER:
            case SHORT:
            case BYTE:
            case DOUBLE:
            case FLOAT:
            case BOOLEAN:
            case ENUM:
            case STRING:
            case DATE:
                pops.setValue(pojo, field, getValue(rs, field));
                break;
            case INSTANT:
                pops.setValue(pojo, field, getInstant(rs, column));
                break;
            case LOCAL_DATE_TIME:
                pops.setValue(pojo, field, getLocalDateTime(rs, column));
                break;
            case BYTE_ARRAY:
                pops.setValue(pojo, field, getValue(rs, field));
                break;
            default:
                throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
        }
    }


    /**
     * Extract the value for the given field from a SQL result set.
     *
     * @param rs    The result set
     * @param field The field for which we're reading data
     * @return The data
     * @throws UncaughtOrmException Thrown if we cannot work out how to extract the
     *                      data.
     */
    private Object getValue(ResultSet rs, Field<?, ?> field) throws UncaughtOrmException {
        String column = getFieldId(field);
        try {
            switch (field.getFieldType()) {
                case LONG:
                case INTEGER:
                case SHORT:
                case BYTE:
                case DOUBLE:
                case FLOAT:
                case BOOLEAN:
                    return rs.getObject(column);
                case ENUM: {
                    Class javaType = field.getJavaType();
                    if (!javaType.isEnum()) {
                        throw new UncaughtOrmException(format("Field %s is not an enum. BUG!", field.getJavaName()));
                    }
                    String val = rs.getString(column);
                    if (val != null) {
                        return Enum.valueOf(javaType, val);
                    }
                    return null;
                }
                case STRING:
                    return rs.getString(column);
                case DATE:
                    return rs.getDate(column);
                case INSTANT:
                    return getInstant(rs, column);
                case LOCAL_DATE_TIME:
                    return getLocalDateTime(rs, column);
                case BYTE_ARRAY:
                    return rs.getBytes(column);
                default:
                    throw new UncaughtOrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            }
        } catch (SQLException | OrmException ex) {
            throw new UncaughtOrmException(format("Error reading field value from SQL for '%s' (%s)", field.getJavaName(), ex.getMessage()), ex);
        }
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws OrmException {
        try {
            Timestamp value = rs.getTimestamp(column);
            if (value == null) {
                return null;
            }
            return value.toLocalDateTime();
        } catch (SQLException ex) {
            throw new OrmException(format("Could not read timestamp value from SQL (%s)", ex.getMessage()), ex);
        }
    }

    private Instant getInstant(ResultSet rs, String column) throws OrmException {
        try {
            Timestamp value = rs.getTimestamp(column);
            if (value == null) {
                return null;
            }
            return value.toInstant();
        } catch (SQLException ex) {
            throw new OrmException(format("Could not read timestamp value from SQL (%s)", ex.getMessage()), ex);
        }
    }

    private String getFieldId(Field<?, ?> field) {
        return getFieldId.apply(field);
    }

    private static <O> Constructor<O> findCanononicalConstructor(Table<O> table) throws OrmException {
        try {
            return table.getObjectClass().getDeclaredConstructor(Arrays.stream(table.getObjectClass().getRecordComponents())
                    .map(RecordComponent::getType).toList().toArray(new Class<?>[]{}));
        } catch (NoSuchMethodException e) {
            throw new OrmException(e.getMessage(), e);
        }
    }

}
