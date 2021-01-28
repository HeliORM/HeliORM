package com.heliorm.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.heliorm.impl.Part;

public class PartExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        if ( Part.class.isAssignableFrom(fieldAttributes.getDeclaringClass())  ) {
            switch (fieldAttributes.getName()) {
                case "selector" : return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
