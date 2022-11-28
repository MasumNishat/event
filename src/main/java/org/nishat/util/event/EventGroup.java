package org.nishat.util.event;

import org.nishat.util.event.Event;
import org.nishat.util.log.Log;

import java.util.HashMap;

public abstract class EventGroup {

    private final HashMap<String, Event> eventHashMap = new HashMap<>();

    /**
     * name of group
     *
     * @return String
     */
    public abstract String name();

    /**
     * callback will execute after every event execution
     *
     * @param eventName String
     */
    public void callback(String eventName) {
    }

    /**
     * if this group is protected then delete operation will be disabled
     * @return boolean
     */
    public boolean isProtected() {
        return false;
    }

    /**
     * Register or update event to group. if group doesn't exist, it will create it.
     *
     * @param event org.nishat.util.event.Event
     */
    public final void addOrUpdate(Event event) {
        if (eventHashMap.containsKey(event.name())) {
            eventHashMap.replace(event.name(), event);
            Log.t("Renewed org.nishat.util.event.Event", event.name()+" in group "+ name());
        }
        else {
            eventHashMap.put(event.name(), event);
            Log.t("Registered org.nishat.util.event.Event", event.name()+" in group "+ name());
        }
    }

    /**
     * Remove event from group
     *
     * @param eventName String
     */
    public void remove(String eventName) {
        if (isProtected()) throw new RuntimeException("Cannot remove event from protected group");
        eventHashMap.remove(eventName);
    }

    /**
     * Remove event from group
     *
     * @param eventName String
     */
    public Event getEvent(String eventName) {
        return eventHashMap.get(eventName);
    }


}
