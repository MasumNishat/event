package org.nishat.util.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ListenerTest {

    @Test
    void testListenerWithDefaultId() {
        Listener listener = new Listener() {
            @Override
            public void exec(Object object) {
            }
        };

        assertNotNull(listener.getId());
        // Should be a valid UUID
        assertDoesNotThrow(() -> UUID.fromString(listener.getId()));
    }

    @Test
    void testListenerWithCustomId() {
        Listener listener = new Listener("customId") {
            @Override
            public void exec(Object object) {
            }
        };

        assertEquals("customId", listener.getId());
    }

    @Test
    void testListenerWithNullIdThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new Listener(null) {
                @Override
                public void exec(Object object) {
                }
            };
        });
    }

    @Test
    void testListenerWithEmptyIdThrowsException() {
        assertThrows(EventException.class, () -> {
            new Listener("") {
                @Override
                public void exec(Object object) {
                }
            };
        });
    }

    @Test
    void testListenerExecution() throws Throwable {
        final boolean[] executed = {false};

        Listener listener = new Listener() {
            @Override
            public void exec(Object object) {
                executed[0] = true;
            }
        };

        listener.exec(null);
        assertTrue(executed[0], "Listener should execute");
    }

    @Test
    void testListenerWithPayload() throws Throwable {
        final String[] result = {null};

        Listener listener = new Listener() {
            @Override
            public void exec(Object object) {
                result[0] = (String) object;
            }
        };

        listener.exec("testPayload");
        assertEquals("testPayload", result[0]);
    }

    @Test
    void testDoBeforeCall() throws Throwable {
        final boolean[] beforeCalled = {false};

        Listener listener = new Listener() {
            @Override
            public void exec(Object object) {
            }

            @Override
            public void doBeforeCall() {
                beforeCalled[0] = true;
            }
        };

        listener.doBeforeCall();
        assertTrue(beforeCalled[0]);
    }

    @Test
    void testDoAfterCall() throws Throwable {
        final boolean[] afterCalled = {false};

        Listener listener = new Listener() {
            @Override
            public void exec(Object object) {
            }

            @Override
            public void doAfterCall() {
                afterCalled[0] = true;
            }
        };

        listener.doAfterCall();
        assertTrue(afterCalled[0]);
    }

    @Test
    void testToString() {
        Listener listener = new Listener("testId") {
            @Override
            public void exec(Object object) {
            }
        };

        String str = listener.toString();
        assertTrue(str.contains("testId"));
    }

    @Test
    void testEquals() {
        Listener listener1 = new Listener("sameId") {
            @Override
            public void exec(Object object) {
            }
        };

        Listener listener2 = new Listener("sameId") {
            @Override
            public void exec(Object object) {
            }
        };

        assertEquals(listener1, listener2);
    }

    @Test
    void testNotEquals() {
        Listener listener1 = new Listener("id1") {
            @Override
            public void exec(Object object) {
            }
        };

        Listener listener2 = new Listener("id2") {
            @Override
            public void exec(Object object) {
            }
        };

        assertNotEquals(listener1, listener2);
    }

    @Test
    void testHashCode() {
        Listener listener1 = new Listener("sameId") {
            @Override
            public void exec(Object object) {
            }
        };

        Listener listener2 = new Listener("sameId") {
            @Override
            public void exec(Object object) {
            }
        };

        assertEquals(listener1.hashCode(), listener2.hashCode());
    }
}
