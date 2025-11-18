package org.nishat.util.event;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EventGroupTest {

    @Test
    void testEventGroupName() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "testGroup";
            }
        };

        assertEquals("testGroup", group.name());
    }

    @Test
    void testEventGroupNotProtectedByDefault() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "test";
            }
        };

        assertFalse(group.isProtected());
    }

    @Test
    void testEventGroupProtected() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "protected";
            }

            @Override
            public boolean isProtected() {
                return true;
            }
        };

        assertTrue(group.isProtected());
    }

    @Test
    void testAddEvent() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "test";
            }
        };

        Event event = new Event("testEvent");
        group.addOrUpdate(event);

        Optional<Event> retrieved = group.getEvent("testEvent");
        assertTrue(retrieved.isPresent());
        assertEquals("testEvent", retrieved.get().name());
    }

    @Test
    void testRemoveEvent() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "test";
            }
        };

        Event event = new Event("toRemove");
        event.setGroupName("test");
        group.addOrUpdate(event);

        group.remove("toRemove");

        Optional<Event> retrieved = group.getEvent("toRemove");
        assertFalse(retrieved.isPresent());
    }

    @Test
    void testRemoveFromProtectedGroupThrowsException() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "protected";
            }

            @Override
            public boolean isProtected() {
                return true;
            }
        };

        Event event = new Event("test");
        group.addOrUpdate(event);

        assertThrows(ProtectedGroupException.class, () -> group.remove("test"));
    }

    @Test
    void testRemoveNonExistentEventThrowsException() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "test";
            }
        };

        assertThrows(EventNotFoundException.class, () -> group.remove("nonExistent"));
    }

    @Test
    void testRemoveNullEventNameThrowsException() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "test";
            }
        };

        assertThrows(NullPointerException.class, () -> group.remove(null));
    }

    @Test
    void testGetNonExistentEvent() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "test";
            }
        };

        Optional<Event> event = group.getEvent("nonExistent");
        assertFalse(event.isPresent());
    }

    @Test
    void testAddNullEventThrowsException() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "test";
            }
        };

        assertThrows(NullPointerException.class, () -> group.addOrUpdate(null));
    }

    @Test
    void testToString() {
        EventGroup group = new EventGroup() {
            @Override
            public String name() {
                return "test";
            }
        };

        String str = group.toString();
        assertTrue(str.contains("test"));
        assertTrue(str.contains("protected=false"));
    }

    @Test
    void testEquals() {
        EventGroup group1 = new EventGroup() {
            @Override
            public String name() {
                return "same";
            }
        };

        EventGroup group2 = new EventGroup() {
            @Override
            public String name() {
                return "same";
            }
        };

        // Note: equals checks events too, so empty groups with same name are equal
        assertEquals(group1, group2);
    }

    @Test
    void testHashCode() {
        EventGroup group1 = new EventGroup() {
            @Override
            public String name() {
                return "same";
            }
        };

        EventGroup group2 = new EventGroup() {
            @Override
            public String name() {
                return "same";
            }
        };

        assertEquals(group1.hashCode(), group2.hashCode());
    }
}
