package org.nishat.util.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link EventManager} is a manager to manage {@link Event}/{@link Listener}. Example:
 * <pre>
 *     //create {@link EventGroup} "bar"
 *     EventGroup evg = new EventGroup() {
 *            {@literal @}Override
 *             public String name() {
 *                 return "bar";
 *             }
 *         };
 *
 *     //create {@link Event} "foo"
 *     Event ev = new Event("foo");
 *
 *     //Register an {@link Event} "foo" in {@link EventGroup} "bar"
 *     EventManager.getInstance().registerEvent(evg, ev);
 *
 *     //check if {@link EventGroup} "bar" exist
 *     Optional&lt;EventGroup&gt; optEvg = EventManager.getInstance().getGroup("bar");
 *     if (optEvg.isPresent()) {
 *          //retrieve {@link Event}
 *          Optional&lt;Event&gt; optEv = optEvg.get().getEvent("foo");
 *          //check if {@link Event} is valid
 *          if (optEv.isPresent()) {
 *              //create {@link Listener}
 *
 *              //register {@link Listener}
 *
 *          }
 *      }
 *
 *      //call {@link Event}
 *      try {
 *             EventManager.getInstance().getEvent("foo").ifPresent(event -> {
 *                 try {
 *                     event.call(null);
 *                 } catch (Throwable e) {
 *                     e.printStackTrace();
 *                 }
 *             });
 *         } catch (Exception e) {
 *             e.printStackTrace();
 *         }
 *
 *
 * </pre>
 */
public final class EventManager {
    /** Logger instance for this class. */
    private static final Logger logger = LoggerFactory.getLogger(EventManager.class);

    /** The default event group name. Events are registered in this group unless otherwise specified. */
    public static final String DEFAULT_GROUP = "default";

    /** The system event group name. This is a protected group that cannot be modified. */
    public static final String SYSTEM_GROUP = "system";

    /** The temporary event group name. Used for temporary events that may be cleaned up. */
    public static final String TEMP_GROUP = "temp";

    /** Singleton instance using double-checked locking pattern. */
    private static volatile EventManager instance;

    /** Thread-safe map storing all event groups, keyed by group name. */
    private final ConcurrentHashMap<String, EventGroup> groups = new ConcurrentHashMap<>();

    /**
     * Get singleton instance of EventManager using double-checked locking.
     *
     * @return {@link EventManager}
     */
    public static EventManager getInstance() {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }

    /**
     * Private constructor to prevent direct instantiation.
     * Initializes the default event groups.
     */
    private EventManager() {
        initializeDefaultGroups();
    }

    /**
     * Initialize the default event groups (default, system, and temp).
     */
    private void initializeDefaultGroups() {
        groups.put(DEFAULT_GROUP, new EventGroup() {
            @Override
            public String name() {
                return DEFAULT_GROUP;
            }
        });

        groups.put(SYSTEM_GROUP, new EventGroup() {
            @Override
            public String name() {
                return SYSTEM_GROUP;
            }

            @Override
            public boolean isProtected() {
                return true;
            }
        });

        groups.put(TEMP_GROUP, new EventGroup() {
            @Override
            public String name() {
                return TEMP_GROUP;
            }
        });
    }

    /**
     * Register an {@link Event}. uses:
     * <pre>
     *     Event ev = new Event("foo");
     *     EventManager.getInstance().registerEvent(ev);
     * </pre>
     * by default {@link Event} will be registered in "default" {@link EventGroup}
     * @param event {@link Event}
     */
    public void registerEvent(Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
        logger.debug("Registering Event: {}", event.name());

        if (!event.getGroupNames().isEmpty()) {
            throw new EventException("Event: " + event.name() + " has predefined group. To register this event in " +
                    "different group, you should use registerEvent(String, Event) function.");
        }

        registerEvent(DEFAULT_GROUP, event);
        logger.info("Registered Event: {}", event.name());
    }

    /**
     * Register an {@link Event}. uses:
     * <pre>
     *     Event ev = new Event("foo");
     *     EventManager.getInstance().registerEvent("bar", ev);
     * </pre>
     * by default {@link EventGroup} "bar" will be created if not exist
     * @param group {@link String}
     * @param event {@link Event}
     */
    public void registerEvent(String group, Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
        Objects.requireNonNull(group, "Group cannot be null");

        if (group.trim().isEmpty()) {
            throw new EventException("Group should not be empty");
        }

        logger.debug("Registering Event: {} [Group: {}]", event.name(), group);

        if (!groups.containsKey(group)) {
            logger.debug("Registering Group: {}", group);
            groups.put(group, new EventGroup() {
                @Override
                public String name() {
                    return group;
                }
            });
            logger.info("Registered Group: {}", group);
        }

        groups.get(group).addOrUpdate(event.setGroupName(group));
        logger.info("Registered Event: {} [Group: {}]", event.name(), group);
    }

    /**
     * Register an {@link Event}. uses:
     * <pre>
     *     EventGroup evg = new EventGroup() {
     *            {@literal @}Override
     *             public String name() {
     *                 return "bar";
     *             }
     *         };
     *     Event ev = new Event("foo");
     *     EventManager.getInstance().registerEvent(evg, ev);
     * </pre>
     * by default {@link EventGroup} "evg" will be registered if not exist
     * @param group {@link EventGroup}
     * @param event {@link Event}
     */
    public void registerEvent(EventGroup group, Event event) {
        Objects.requireNonNull(group, "Group cannot be null");
        Objects.requireNonNull(event, "Event cannot be null");

        logger.debug("Registering Event: {} [Group: {}]", event.name(), group.name());

        if (!groups.containsKey(group.name())) {
            logger.debug("Registering Group: {}", group.name());
            groups.put(group.name(), group);
            logger.info("Registered Group: {}", group.name());
        }

        groups.get(group.name()).addOrUpdate(event.setGroupName(group.name()));
        logger.info("Registered Event: {} [Group: {}]", event.name(), group.name());
    }

    /**
     * Unregister an {@link Event}. uses:
     * <pre>
     *     EventManager.getInstance().unregisterEvent("foo");
     * </pre>
     * by default {@link Event} will be unregistered from "default" {@link EventGroup}
     * @param eventName {@link String}
     */
    public void unregisterEvent(String eventName) {
        unregisterEvent(DEFAULT_GROUP, eventName);
    }

    /**
     * Unregister an {@link Event}. uses:
     * <pre>
     *     EventManager.getInstance().unregisterEvent("bar", "foo");
     * </pre>
     * @param group {@link String}
     * @param eventName {@link String}
     */
    public void unregisterEvent(String group, String eventName) {
        Objects.requireNonNull(eventName, "Event name cannot be null");
        Objects.requireNonNull(group, "Group cannot be null");

        if (eventName.trim().isEmpty()) {
            throw new EventException("Event name should not be empty");
        }
        if (group.trim().isEmpty()) {
            throw new EventException("Group should not be empty");
        }

        logger.debug("Unregistering Event: {} [Group: {}]", eventName, group);

        EventGroup eventGroup = groups.get(group);
        if (eventGroup == null) {
            throw new EventGroupNotFoundException(group);
        }

        eventGroup.remove(eventName);
        logger.info("Unregistered Event: {} [Group: {}]", eventName, group);
    }

    /**
     * Get a registered {@link Event}. uses:
     * <pre>
     *     Optional&lt;Event&gt; ev = EventManager.getInstance().getEvent("foo");
     * </pre>
     * by default {@link Event} will be pulled from "default" {@link EventGroup}
     * @param eventName {@link String}
     * @return {@link Optional}&lt;{@link Event}&gt;
     */
    public Optional<Event> getEvent(String eventName) {
        return getEvent(DEFAULT_GROUP, eventName);
    }

    /**
     * Get a registered {@link Event}. uses:
     * <pre>
     *     Optional&lt;Event&gt; ev = EventManager.getInstance().getEvent("bar", "foo");
     * </pre>
     * @param group {@link String}
     * @param eventName {@link String}
     * @return {@link Optional}&lt;{@link Event}&gt;
     */
    public Optional<Event> getEvent(String group, String eventName) {
        return getGroup(group)
                .flatMap(g -> g.getEvent(eventName));
    }

    /**
     * Move {@link Event} from one {@link EventGroup} to another {@link EventGroup}. both {@link EventGroup} must exist. uses:
     * <pre>
     *     EventManager.getInstance().moveToGroup("bar1", "bar2", "foo");
     * </pre>
     * @param fromGroup {@link String}
     * @param toGroup   {@link String}
     * @param eventName {@link String}
     */
    public void moveToGroup(String fromGroup, String toGroup, String eventName) {
        Objects.requireNonNull(fromGroup, "From group cannot be null");
        Objects.requireNonNull(toGroup, "To group cannot be null");
        Objects.requireNonNull(eventName, "Event name cannot be null");

        logger.debug("Moving Event: {} [From: {}] [To: {}]", eventName, fromGroup, toGroup);

        if (!groups.containsKey(fromGroup)) {
            throw new EventGroupNotFoundException(fromGroup);
        }
        if (!groups.containsKey(toGroup)) {
            throw new EventGroupNotFoundException(toGroup);
        }

        Event event = groups.get(fromGroup).getEvent(eventName)
                .orElseThrow(() -> new EventNotFoundException(eventName, fromGroup));

        groups.get(toGroup).addOrUpdate(event);
        groups.get(fromGroup).remove(eventName);

        logger.info("Moved Event: {} [From: {}] [To: {}]", eventName, fromGroup, toGroup);
    }

    /**
     * List of all available {@link EventGroup}. Uses:
     * <pre>
     *     ArrayList&lt;EventGroup&gt; evgs = listGroup();
     * </pre>
     * @return {@link ArrayList}&lt;{@link EventGroup}&gt;
     */
    public ArrayList<EventGroup> listGroup() {
        return new ArrayList<>(groups.values());
    }

    /**
     * Retrieve an {@link EventGroup}. Uses:
     * <pre>
     *     Optional&lt;EventGroup&gt; evg = getGroup("bar");
     * </pre>
     * @param group {@link String}
     * @return {@link Optional}&lt;{@link EventGroup}&gt;
     */
    public Optional<EventGroup> getGroup(String group) {
        return Optional.ofNullable(groups.get(group));
    }

    @Override
    public String toString() {
        return "EventManager{" +
                "groups=" + groups.keySet() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventManager that = (EventManager) o;
        return Objects.equals(groups, that.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groups);
    }
}
