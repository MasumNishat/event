package org.nishat.util.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EventManagerTest {

    private EventManager eventManager;

    @BeforeEach
    void setUp() {
        eventManager = EventManager.getInstance();
    }

    @Test
    void testSingletonInstance() {
        EventManager instance1 = EventManager.getInstance();
        EventManager instance2 = EventManager.getInstance();
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }

    @Test
    void testRegisterEventWithDefaultGroup() {
        Event event = new Event("testEvent");
        eventManager.registerEvent(event);

        Optional<Event> retrieved = eventManager.getEvent("testEvent");
        assertTrue(retrieved.isPresent(), "Event should be registered");
        assertEquals("testEvent", retrieved.get().name());
    }

    @Test
    void testRegisterEventWithCustomGroup() {
        Event event = new Event("testEvent2");
        eventManager.registerEvent("customGroup", event);

        Optional<Event> retrieved = eventManager.getEvent("customGroup", "testEvent2");
        assertTrue(retrieved.isPresent(), "Event should be registered in custom group");
    }

    @Test
    void testRegisterEventWithEventGroup() {
        EventGroup customGroup = new EventGroup() {
            @Override
            public String name() {
                return "myGroup";
            }
        };

        Event event = new Event("groupEvent");
        eventManager.registerEvent(customGroup, event);

        Optional<Event> retrieved = eventManager.getEvent("myGroup", "groupEvent");
        assertTrue(retrieved.isPresent(), "Event should be registered with EventGroup");
    }

    @Test
    void testUnregisterEvent() {
        Event event = new Event("toRemove");
        eventManager.registerEvent(event);

        eventManager.unregisterEvent("toRemove");

        Optional<Event> retrieved = eventManager.getEvent("toRemove");
        assertFalse(retrieved.isPresent(), "Event should be unregistered");
    }

    @Test
    void testUnregisterEventFromNonExistentGroupThrowsException() {
        assertThrows(EventGroupNotFoundException.class, () -> {
            eventManager.unregisterEvent("nonExistentGroup", "someEvent");
        });
    }

    @Test
    void testMoveEventBetweenGroups() {
        Event event = new Event("movableEvent");
        eventManager.registerEvent("group1", event);
        eventManager.registerEvent("group2", new Event("dummy"));

        eventManager.moveToGroup("group1", "group2", "movableEvent");

        Optional<Event> fromGroup1 = eventManager.getEvent("group1", "movableEvent");
        Optional<Event> fromGroup2 = eventManager.getEvent("group2", "movableEvent");

        assertFalse(fromGroup1.isPresent(), "Event should be removed from group1");
        assertTrue(fromGroup2.isPresent(), "Event should be in group2");
    }

    @Test
    void testMoveEventWithNonExistentGroup() {
        assertThrows(EventGroupNotFoundException.class, () -> {
            eventManager.moveToGroup("nonExistent", EventManager.DEFAULT_GROUP, "event");
        });
    }

    @Test
    void testMoveEventWithNonExistentEvent() {
        eventManager.registerEvent("group1", new Event("dummy"));
        assertThrows(EventNotFoundException.class, () -> {
            eventManager.moveToGroup(EventManager.DEFAULT_GROUP, "group1", "nonExistentEvent");
        });
    }

    @Test
    void testGetNonExistentEvent() {
        Optional<Event> event = eventManager.getEvent("nonExistent");
        assertFalse(event.isPresent(), "Non-existent event should return empty Optional");
    }

    @Test
    void testGetNonExistentGroup() {
        Optional<EventGroup> group = eventManager.getGroup("nonExistent");
        assertFalse(group.isPresent(), "Non-existent group should return empty Optional");
    }

    @Test
    void testListGroups() {
        assertTrue(eventManager.listGroup().size() >= 3, "Should have at least default, system, and temp groups");
    }

    @Test
    void testDefaultGroupsExist() {
        assertTrue(eventManager.getGroup(EventManager.DEFAULT_GROUP).isPresent());
        assertTrue(eventManager.getGroup(EventManager.SYSTEM_GROUP).isPresent());
        assertTrue(eventManager.getGroup(EventManager.TEMP_GROUP).isPresent());
    }

    @Test
    void testSystemGroupIsProtected() {
        Optional<EventGroup> systemGroup = eventManager.getGroup(EventManager.SYSTEM_GROUP);
        assertTrue(systemGroup.isPresent());
        assertTrue(systemGroup.get().isProtected(), "System group should be protected");
    }

    @Test
    void testRegisterNullEventThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            eventManager.registerEvent((Event) null);
        });
    }

    @Test
    void testRegisterEventWithEmptyGroupNameThrowsException() {
        Event event = new Event("test");
        assertThrows(EventException.class, () -> {
            eventManager.registerEvent("", event);
        });
    }
}
