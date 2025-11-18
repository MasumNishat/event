package org.nishat.util.event;

import java.util.Objects;
import java.util.Optional;
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
 *     Optional&lt;Event&gt; event = evg.getEvent("foo");
 *     if (event.isPresent()) {
 *         //remove event "foo"
 *         evg.remove("foo");
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
     * Default constructor for EventGroup.
     * Subclasses must implement the {@link #name()} method.
     */
    public EventGroup() {
        // Default constructor
    }

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
        Objects.requireNonNull(event, "Event cannot be null");
        eventHashMap.put(event.name(), event);
    }

    /**
     * remove {@link Event} from {@link EventGroup}
     * @param eventName {@link String}
     * @throws ProtectedGroupException if the group is protected
     * @throws EventNotFoundException if the event doesn't exist
     */
    final void remove(String eventName) {
        Objects.requireNonNull(eventName, "Event name cannot be null");

        if (isProtected()) {
            throw new ProtectedGroupException(name());
        }

        Event event = eventHashMap.get(eventName);
        if (event == null) {
            throw new EventNotFoundException(eventName, name());
        }

        event.removeGroup(name());
        eventHashMap.remove(eventName);
    }

    /**
     * get {@link Event} from {@link EventGroup}
     * @param eventName {@link String}
     * @return {@link Optional}&lt;{@link Event}&gt;
     */
    public final Optional<Event> getEvent(String eventName) {
        return Optional.ofNullable(eventHashMap.get(eventName));
    }

    @Override
    public String toString() {
        return "EventGroup{" +
                "name='" + name() + '\'' +
                ", protected=" + isProtected() +
                ", events=" + eventHashMap.keySet() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventGroup)) return false;
        EventGroup that = (EventGroup) o;
        return Objects.equals(name(), that.name()) &&
                Objects.equals(eventHashMap, that.eventHashMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), eventHashMap);
    }
}
