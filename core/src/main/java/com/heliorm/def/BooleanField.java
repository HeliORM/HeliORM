package com.heliorm.def;

import com.heliorm.Table;

/**
 * A field representing a boolean value
 *
 * @param <DT> Table type
 * @param <DO> Object type
 *
 * @author gideon
 */
public interface BooleanField<DT extends Table<DO>, DO> extends Field<DT, DO, Boolean>, Expression<DT, DO, Boolean>, WithEquals<DT, DO, Boolean>, WithIs<DT, DO, Boolean> {

    @Override
   default FieldType getFieldType() {
        return FieldType.BOOLEAN;
    }
}
