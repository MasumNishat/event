package org.nishat.util.event;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link EventGroup} is a grouping system of  {@link Event}/{@link Listener}. Example:
 * <pre>
 *     //create {@link EventGroup} "bar"
 *     EventGroup evg = new EventGroup() {
 *            {@literal @}Override
 *             public String name() {
 *                 return "bar";
 *             }
 *         };
 *     //check if an event "foo" exist in this event group
 *     if (evg.getEvent("foo") != null) {
 *         //remove event "foo"
 *         evg.remove("foo);
 *     }
 *
 *     //check if group is protected
 *     if (evg.isProtected()) {
 *         //get event name
 *         String name = evg.name();
 *     }
 * </pre>
 */
public abstract class EventGroup {
    private final ConcurrentHashMap<String, Event> eventHashMap = new ConcurrentHashMap<>();

    /**
     * Get name of {@link EventGroup}
     * @return {@link String}
     */
    public abstract String name();

    /**
     * if this {@link EventGroup} is protected then delete operation will be disabled.
     * <p>true: protected</p>
     * <p>false: not protected</p>
     * Default value is false
     * @return boolean
     */
    public boolean isProtected() {
        return false;
    }

    final void addOrUpdate(Event event) {
        eventHashMap.put(event.name(), event);
    }

    /**
     * remove {@link Event} from {@link EventGroup}
     * @param eventName {@link String}
     */
    final void remove(String eventName) {
        Objects.requireNonNull(eventName);
        if (isProtected()) throw new RuntimeException("Cannot remove event from protected group");
        eventHashMap.get(eventName).removeGroup(name());
        eventHashMap.remove(eventName);
    }

    /**
     * get {@link Event} from {@link EventGroup}
     * @param eventName {@link String}
     * @return {@link Event} or null if {@link Event} not exist
     */
    public final Event getEvent(String eventName) {
        return eventHashMap.get(eventName);
    }
}
