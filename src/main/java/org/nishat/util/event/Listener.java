package org.nishat.util.event;

import java.util.UUID;

public abstract class Listener {
    private final String id;
    public Listener() {
        id = UUID.randomUUID().toString();
    }
    public Listener(String id){
        this.id = id;
    }
    public abstract void exec(Object object) throws Throwable;
    public final String getId() {
        return id;
    }
}
