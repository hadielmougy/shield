package io.github.shield.internal;

import io.github.shield.Connector;

import java.util.IdentityHashMap;

public class ComponentRegistry {

    public static final ComponentRegistry INSTANCE = new ComponentRegistry();

    private IdentityHashMap<Object, Connector> identityMap = new IdentityHashMap<>();

    public <T> void register(T component, Connector connector) {
        identityMap.put(component, connector);
    }


}
