package com.heliorm.json;

import com.heliorm.impl.Part;

public final class FakePart extends Part {

    private final String id;
    public FakePart(String id) {
        super(null, null);
        this.id  = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "fake";
    }
}
