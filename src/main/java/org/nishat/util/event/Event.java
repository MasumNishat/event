package org.nishat.util.event;

import java.util.concurrent.ConcurrentHashMap;

public class Event {
    private final String name;
    private final ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<>();
    public Event(String name) {
        this.name = name;
    }
    public final String name(){
        return name;
    }
    private boolean breakLoop = false;
    public final void addOrReplaceListener(Listener listener) {
        if (listeners.containsKey(listener.getId())) {
            listeners.replace(listener.getId(), listener);
        } else {
            listeners.put(listener.getId(), listener);
        }
    }
    public final void addListener(Listener listener) {
        if (!listeners.containsKey(listener.getId())) listeners.put(listener.getId(), listener);
    }
    public final void removeListener(Listener listener) {
        if (!listeners.containsKey(listener.getId())) listeners.remove(listener.getId());
    }
    public final void removeListener(String id) {
        if (!listeners.containsKey(id)) listeners.remove(id);
    }
    public final void call(Object payload) throws Throwable {
        ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<>(this.listeners);
        for (Listener listener : listeners.values()) {
            if (breakLoop) {
                breakLoop = false;
                break;
            }
            listener.exec(payload);
        }
    }
    public final void breakLoop() {
        breakLoop = true;
    }
    public final boolean hasListener(String id) {
        return listeners.containsKey(id);
    }
    public final boolean hasListener(Listener listener) {
        return listeners.containsKey(listener.getId());
    }

}
