package com.heliorm.def;

import com.heliorm.Table;

/**
 * A field representing a enum value
 *
 * @param <T> Table type
 * @param <O> Object type
 *
 * @author gideon
 */
public interface EnumField<T extends Table<O>, O, C extends Enum> extends Field<T, O, C>, WithEquals<T, O, C>, WithIn<T, O, C>, WithIs<T, O, C> {

    @Override
   default FieldType getFieldType() {
        return FieldType.ENUM;
    }


}
