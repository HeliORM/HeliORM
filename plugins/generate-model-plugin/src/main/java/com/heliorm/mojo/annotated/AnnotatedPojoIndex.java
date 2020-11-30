package com.heliorm.mojo.annotated;

import com.heliorm.def.Field;
import com.heliorm.def.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class AnnotatedPojoIndex implements Index {

    private final boolean unique;
    private final List<Field> fields;

    public AnnotatedPojoIndex(AnnotatedPojoTable table, com.heliorm.annotation.Index ann) {
        unique = ann.unique();
        fields = new ArrayList<>();
        Map<String, Field> fields = table.getFields().stream()
                .collect(Collectors.toMap(field -> field.getJavaName(), field -> field));
        for (String indexField : ann.columns()) {
            Field field = fields.get(indexField);
            if (field == null) {
                throw new AnnotatedPojoException(format("Cannot find index field '%s' on POJO %s", indexField, table.getObjectClass().getSimpleName()));
            }
            this.fields.add(field);
        }
    }

    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }
}