package com.heliorm.sql.structure;

import com.heliorm.Field;
import com.heliorm.sql.StringColumn;

public class StructureStringColumn extends StructureColumn implements StringColumn {

    public StructureStringColumn(StructureTable table, Field<?,?,?> field) {
        super(table, field);
    }

    @Override
    public int getLength() {
        return field.getLength().orElse(255);
    }
}
