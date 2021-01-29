package com.heliorm.json;

import com.heliorm.impl.Part;

public final class FakePart extends Part {

    public static FakePart TEMP = new FakePart(null, null);

    private FakePart(Type type, Part left) {
        super(type, left);
    }

    @Override
    public String toString() {
        return "fake";
    }
}
