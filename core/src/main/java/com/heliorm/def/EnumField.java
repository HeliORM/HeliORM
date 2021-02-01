package com.heliorm.def;

import com.heliorm.Table;

/**
 * @author gideon
 */
public interface EnumField<T extends Table<O>, O, C extends Enum> extends Field<T, O, C>, EnumExpression<T, O, C> {

    @Override
   default FieldType getFieldType() {
        return FieldType.ENUM;
    }


}
