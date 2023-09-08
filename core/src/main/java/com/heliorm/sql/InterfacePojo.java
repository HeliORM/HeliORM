package com.heliorm.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class InterfacePojo<O> implements InvocationHandler {

    private final Class<O> type;
    private final Map<String, Object> values = new HashMap<>();

    public InterfacePojo(Class<O> type) {
        this.type = type;
    }

    public void set(String name, Object value) {
        values.put(name,value);
    }

    public Object get(String name) {
        return values.get(name);
    }

    public Class<O> getType() {
        return type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return values.get(method.getName());
    }
}
