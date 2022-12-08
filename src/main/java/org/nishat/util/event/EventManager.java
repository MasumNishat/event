package org.nishat.util.event;

import org.nishat.util.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
 *     evg = EventManager.getInstance().getGroup("bar");
 *     if (evg != null) {
 *          //retrieve {@link Event}
 *          ev = evg.getEvent("foo");
 *          //check if {@link Event} is valid
 *          if (ev != null) {
 *              //create {@link Listener}
 *
 *              //register {@link Listener}
 *
 *          }
 *      }
 *
 *      //call {@link Event}
 *
 *
 *
 * </pre>
 */
public class EventManager {
    private static EventManager single_instance = null;

    private final HashMap<String, EventGroup> groups = new HashMap<String, EventGroup>(){{
        put("default", new EventGroup() {
            /**
             * Get name of {@link EventGroup}
             * @return {@link String}
             */
            @Override
            public String name() {
                return "default";
            }
        });
        put("system", new EventGroup() {
            /**
             * Get name of {@link EventGroup}
             * @return {@link String}
             */
            @Override
            public String name() {
                return "system";
            }

            /**
             * if this {@link EventGroup} is protected then delete operation will be disabled.
             * <p>true: protected</p>
             * <p>false: not protected</p>
             * Default value is false
             * @return boolean
             */
            @Override
            public boolean isProtected() {
                return true;
            }
        });
        put("temp", new EventGroup() {
            /**
             * Get name of {@link EventGroup}
             * @return {@link String}
             */
            @Override
            public String name() {
                return "temp";
            }
        });
    }};

    /**
     * Initial Constructor
     *
     * @return {@link EventManager}
     */
    public static EventManager getInstance() {
        if (single_instance == null)
            single_instance = new EventManager();
        return single_instance;
    }

    private EventManager() {
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
        Log.i("Registering Event",event.name());
        Objects.requireNonNull(event);
        registerEvent("default", event);
        Log.i("Registered Event",event.name());
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
        Log.i("Registering Event",event.name()+"[Group: "+group+"]");
        Objects.requireNonNull(event);
        Objects.requireNonNull(group);
        if (group.trim().equals("")) throw new RuntimeException("Group should not be empty");
        if (!groups.containsKey(group)) {
            Log.i("Registering Group", group);
            groups.put(group, new EventGroup() {
                /**
                 * Get name of {@link EventGroup}
                 * @return {@link String}
                 */
                @Override
                public String name() {
                    return group;
                }
            });
            Log.i("Registered Group", group);
        }
        groups.get(group).addOrUpdate(event);
        Log.i("Registered Event",event.name()+"[Group: "+group+"]");
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
        Log.i("Registering Event",event.name()+"[Group: "+group.name()+"]");
        Objects.requireNonNull(group);
        Objects.requireNonNull(event);

        if (!groups.containsKey(group.name())) {
            Log.i("Registering Group", group.name());
            groups.put(group.name(), group);
            Log.i("Registered Group", group.name());
        }
        groups.get(group.name()).addOrUpdate(event);
        Log.i("Registered Event",event.name()+"[Group: "+group.name()+"]");
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
        unregisterEvent("default", eventName);
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
        Log.i("Unregistering Event",eventName+"[Group: "+group+"]");
        Objects.requireNonNull(eventName);
        if (eventName.trim().equals("")) throw new RuntimeException("eventName should not be empty");
        Objects.requireNonNull(group);
        if (group.trim().equals("")) throw new RuntimeException("Group should not be empty");
        groups.get(group).remove(eventName);
        Log.i("Unregistered Event",eventName+"[Group: "+group+"]");
    }

    /**
     * Get a registered {@link Event}. uses:
     * <pre>
     *     Event ev = EventManager.getInstance().getEvent("foo");
     * </pre>
     * by default {@link Event} will be pulled from "default" {@link EventGroup}
     * @param eventName {@link String}
     * @return {@link Event}
     */
    public Event getEvent(String eventName) {
        return getGroup("default").getEvent(eventName);
    }

    /**
     * Get a registered {@link Event}. uses:
     * <pre>
     *     Event ev = EventManager.getInstance().getEvent("bar", "foo");
     * </pre>
     * @param group {@link String}
     * @param eventName {@link String}
     * @return {@link Event}
     */
    public Event getEvent(String group, String eventName) {
        return getGroup(group).getEvent(eventName);
    }

    /**
     * Move {@link Event} from one {@link EventGroup} to another {@link EventGroup}. both {@link EventGroup} must be existed. uses:
     * <pre>
     *     EventManager.getInstance().moveToGroup("bar1", "bar2", "foo");
     * </pre>
     * @param fromGroup {@link String}
     * @param toGroup   {@link String}
     * @param eventName {@link String}
     */
    public void moveToGroup(String fromGroup, String toGroup, String eventName) {
        Log.i("Moving Event",eventName+"[From: "+fromGroup+"] [To: "+toGroup+"]");
        if (groups.containsKey(fromGroup) && groups.containsKey(toGroup)) {
            groups.get(toGroup).addOrUpdate(groups.get(fromGroup).getEvent(eventName));
            groups.get(fromGroup).remove(eventName);
        } else throw new RuntimeException("Group not found");
        Log.i("Moved Event",eventName+"[From: "+fromGroup+"] [To: "+toGroup+"]");
    }

    /**
     * List of all available {@link EventGroup}. Uses:
     * <pre>
     *     ArrayList&lt;EventGroup> evgs = listGroup();
     * </pre>
     * @return {@link ArrayList}&lt;{@link EventGroup}&gt;
     */
    public ArrayList<EventGroup> listGroup() {
        return new ArrayList<>(groups.values());
    }

    /**
     * Retrieve an {@link EventGroup}. Uses:
     * <pre>
     *     EventGroup evg = getGroup("bar");
     * </pre>
     * @param group {@link String}
     * @return {@link EventGroup} or null if event not exist
     */
    public EventGroup getGroup(String group) {
        return groups.get(group);
    }
}
