package com.heliorm.sql.structure;

import com.heliorm.Field;
import com.heliorm.sql.EnumColumn;

import java.util.Set;

public class StructureEnumColumn  extends StructureColumn implements EnumColumn {

    private final Set<String> enumValues;

    public StructureEnumColumn(StructureTable table, Field field, Set<String> enumValues) {
        super(table, field);
        this.enumValues = enumValues;
    }

    @Override
    public Set<String> getEnumValues() {
        return enumValues;
    }
}
