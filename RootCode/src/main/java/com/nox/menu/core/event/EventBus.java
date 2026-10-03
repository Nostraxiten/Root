/*
 * Decompiled with CFR 0.152.
 */
package com.nox.menu.core.event;

import com.nox.menu.core.event.Event;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    private static final EventBus INSTANCE = new EventBus();
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> listeners = new ConcurrentHashMap<Class<? extends Event>, List<Consumer<? extends Event>>>();

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public <T extends Event> void subscribe(Class<T> eventClass, Consumer<T> listener) {
        this.listeners.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList()).add(listener);
    }

    public <T extends Event> void unsubscribe(Class<T> eventClass, Consumer<T> listener) {
        List<Consumer<? extends Event>> list = this.listeners.get(eventClass);
        if (list != null) {
            list.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> T post(T event) {
        List<Consumer<? extends Event>> list = this.listeners.get(event.getClass());
        if (list != null) {
            for (Consumer<? extends Event> consumer : list) {
                ((Consumer<T>) consumer).accept(event);
            }
        }
        return event;
    }
}

