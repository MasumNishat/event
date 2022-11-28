package org.nishat.util.event;

import org.nishat.util.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class EventManager {
    private static EventManager single_instance = null;

    private final HashMap<String, EventGroup> groups = new HashMap<String, EventGroup>(){{
        put("default", new EventGroup() {
            /**
             * name of group
             *
             * @return String
             */
            @Override
            public String name() {
                return "default";
            }
        });
        put("system", new EventGroup() {
            /**
             * name of group
             *
             * @return String
             */
            @Override
            public String name() {
                return "system";
            }

            /**
             * if this group is protected then delete operation will be disabled
             *
             * @return boolean
             */
            @Override
            public boolean isProtected() {
                return true;
            }
        });
        put("temp", new EventGroup() {
            /**
             * name of group
             *
             * @return String
             */
            @Override
            public String name() {
                return "temp";
            }

            /**
             * callback will execute after every event execution
             *
             * @param eventName String
             */
            @Override
            public void callback(String eventName) {
                super.remove(eventName);
            }
        });
    }};

    /**
     * Initial Constructor
     *
     * @return org.nishat.util.event.EventManager
     */
    public static EventManager getInstance() {
        if (single_instance == null)
            single_instance = new EventManager();
        return single_instance;
    }

    private EventManager() {
    }

    public void registerEvent(Event event) {
        Objects.requireNonNull(event);
        registerEvent("default", event);
    }

    public void registerEvent(String group, Event event) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(group);
        if (group.trim().equals("")) throw new RuntimeException("Group should not be empty");
        if (!groups.containsKey(group)) {
            groups.put(group, new EventGroup() {
                @Override
                public String name() {
                    return group;
                }
            });
        }
        groups.get(group).addOrUpdate(event);
    }

    public void registerEvent(EventGroup group, Event event) {
        Objects.requireNonNull(group);
        Objects.requireNonNull(event);

        if (!groups.containsKey(group.name())) {
            groups.put(group.name(), group);
        }
        groups.get(group.name()).addOrUpdate(event);
        Log.t("Registered org.nishat.util.event.Event", event.name());
    }

    public void unregisterEvent(String eventName) {
        unregisterEvent("default", eventName);
    }

    public void unregisterEvent(String group, String eventName) {
        Objects.requireNonNull(eventName);
        if (eventName.trim().equals("")) throw new RuntimeException("eventName should not be empty");
        Objects.requireNonNull(group);
        if (group.trim().equals("")) throw new RuntimeException("Group should not be empty");
        groups.get(group).remove(eventName);
        Log.t("Unregistered org.nishat.util.event.Event", eventName);
    }

    public Event getEvent(String eventName) {
        return getGroup("default").getEvent(eventName);
    }

    public Event getEvent(String group, String eventName) {
        return getGroup(group).getEvent(eventName);
    }

    /**
     * Move event from one group to another. both groups must be existed
     *
     * @param fromGroup String
     * @param toGroup   String
     * @param eventName String
     */
    public void moveToGroup(String fromGroup, String toGroup, String eventName) {
        if (groups.containsKey(fromGroup) && groups.containsKey(toGroup)) {
            groups.get(toGroup).addOrUpdate(groups.get(fromGroup).getEvent(eventName));
            groups.get(fromGroup).remove(eventName);
        } else throw new RuntimeException("Group not found");
    }

    /**
     * Check if a group exist
     *
     * @param group String
     */
    public boolean isGroupExist(String group) {
        return groups.containsKey(group);
    }

    /**
     * List of all available groups
     *
     * @return ArrayList&lt;org.nishat.util.event.EventGroup&gt;
     */
    public ArrayList<EventGroup> listGroup() {
        return new ArrayList<>(groups.values());
    }

    /**
     * Retrieve a group
     *
     * @param group String
     */
    public EventGroup getGroup(String group) {
        if (groups.containsKey(group)) {
            return groups.get(group);
        } else throw new RuntimeException("Group not found");
    }
}
