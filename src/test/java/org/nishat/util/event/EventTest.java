package org.nishat.util.event;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testEventCreation() {
        Event event = new Event("testEvent");
        assertEquals("testEvent", event.name());
    }

    @Test
    void testEventBuilderDefaultConfiguration() {
        Event event = Event.builder("builderEvent").build();
        assertEquals("builderEvent", event.name());
    }

    @Test
    void testEventBuilderWithExceptionHandling() {
        Event event = Event.builder("exceptionEvent")
                .withExceptionHandling(true)
                .build();
        assertNotNull(event);
    }

    @Test
    void testAddListener() {
        Event event = new Event("test");
        Listener listener = new Listener() {
            @Override
            public void exec(Object object) {
            }
        };

        event.addListener(listener);
        assertTrue(event.hasListener(listener));
    }

    @Test
    void testAddListenerWithPriority() {
        Event event = new Event("test");
        Listener listener = new Listener("priorityListener") {
            @Override
            public void exec(Object object) {
            }
        };

        event.addListener(listener, 10);
        assertTrue(event.hasListener("priorityListener"));
        assertEquals(10, event.getListenerPriority("priorityListener").orElse(-1));
    }

    @Test
    void testRemoveListener() {
        Event event = new Event("test");
        Listener listener = new Listener("removeMe") {
            @Override
            public void exec(Object object) {
            }
        };

        event.addListener(listener);
        assertTrue(event.hasListener("removeMe"));

        event.removeListener(listener);
        assertFalse(event.hasListener("removeMe"));
    }

    @Test
    void testCallListeners() throws Throwable {
        Event event = new Event("test");
        AtomicInteger counter = new AtomicInteger(0);

        event.addListener(new Listener() {
            @Override
            public void exec(Object object) {
                counter.incrementAndGet();
            }
        });

        event.addListener(new Listener() {
            @Override
            public void exec(Object object) {
                counter.incrementAndGet();
            }
        });

        event.call(null);
        assertEquals(2, counter.get(), "Both listeners should be called");
    }

    @Test
    void testListenerPriorityOrder() throws Throwable {
        Event event = new Event("test");
        StringBuilder executionOrder = new StringBuilder();

        event.addListener(new Listener("low") {
            @Override
            public void exec(Object object) {
                executionOrder.append("L");
            }
        }, 1);

        event.addListener(new Listener("high") {
            @Override
            public void exec(Object object) {
                executionOrder.append("H");
            }
        }, 10);

        event.addListener(new Listener("medium") {
            @Override
            public void exec(Object object) {
                executionOrder.append("M");
            }
        }, 5);

        event.call(null);
        assertEquals("HML", executionOrder.toString(), "Listeners should execute in priority order");
    }

    @Test
    void testBreakLoop() throws Throwable {
        Event event = new Event("test");
        AtomicInteger counter = new AtomicInteger(0);

        event.addListener(new Listener("first") {
            @Override
            public void exec(Object object) {
                counter.incrementAndGet();
            }
        }, 3);

        event.addListener(new Listener("second") {
            @Override
            public void exec(Object object) {
                counter.incrementAndGet();
                event.breakLoop();
            }
        }, 2);

        event.addListener(new Listener("third") {
            @Override
            public void exec(Object object) {
                counter.incrementAndGet();
            }
        }, 1);

        event.call(null);
        assertEquals(2, counter.get(), "Only first two listeners should execute");
    }

    @Test
    void testCallAsyncCompletes() throws Exception {
        Event event = new Event("asyncTest");
        AtomicBoolean executed = new AtomicBoolean(false);

        event.addListener(new Listener() {
            @Override
            public void exec(Object object) {
                executed.set(true);
            }
        });

        CompletableFuture<Void> future = event.callAsync(null);
        future.get(5, TimeUnit.SECONDS);

        assertTrue(executed.get(), "Async listener should execute");
    }

    @Test
    void testExceptionHandlingDisabled() {
        Event event = new Event("test");

        event.addListener(new Listener() {
            @Override
            public void exec(Object object) throws Exception {
                throw new RuntimeException("Test exception");
            }
        });

        assertThrows(ListenerExecutionException.class, () -> event.call(null));
    }

    @Test
    void testExceptionHandlingEnabled() throws Throwable {
        Event event = Event.builder("test")
                .withExceptionHandling(true)
                .build();

        AtomicInteger counter = new AtomicInteger(0);

        event.addListener(new Listener("failing") {
            @Override
            public void exec(Object object) throws Exception {
                throw new RuntimeException("Test exception");
            }
        }, 2);

        event.addListener(new Listener("succeeding") {
            @Override
            public void exec(Object object) {
                counter.incrementAndGet();
            }
        }, 1);

        event.call(null);
        assertEquals(1, counter.get(), "Non-failing listener should execute despite exception");
    }

    @Test
    void testBeforeAfterCallHooks() throws Throwable {
        Event event = new Event("test");
        AtomicInteger beforeCounter = new AtomicInteger(0);
        AtomicInteger afterCounter = new AtomicInteger(0);

        event.doBeforeEachCall(e -> beforeCounter.incrementAndGet());
        event.doAfterEachCall(e -> afterCounter.incrementAndGet());

        event.call(null);

        assertEquals(1, beforeCounter.get());
        assertEquals(1, afterCounter.get());
    }

    @Test
    void testBeforeAfterListenerCallHooks() throws Throwable {
        Event event = new Event("test");
        AtomicInteger beforeCounter = new AtomicInteger(0);
        AtomicInteger afterCounter = new AtomicInteger(0);

        event.doBeforeEachListenerCall(l -> beforeCounter.incrementAndGet());
        event.doAfterEachListenerCall(l -> afterCounter.incrementAndGet());

        event.addListener(new Listener() {
            @Override
            public void exec(Object object) {
            }
        });
        event.addListener(new Listener() {
            @Override
            public void exec(Object object) {
            }
        });

        event.call(null);

        assertEquals(2, beforeCounter.get(), "Before hook should run for each listener");
        assertEquals(2, afterCounter.get(), "After hook should run for each listener");
    }

    @Test
    void testUpdateListenerPriority() {
        Event event = new Event("test");
        Listener listener = new Listener("updateMe") {
            @Override
            public void exec(Object object) {
            }
        };

        event.addListener(listener, 5);
        assertEquals(5, event.getListenerPriority("updateMe").orElse(-1));

        boolean updated = event.updateListenerPriority("updateMe", 10);
        assertTrue(updated);
        assertEquals(10, event.getListenerPriority("updateMe").orElse(-1));
    }

    @Test
    void testUpdateNonExistentListenerPriority() {
        Event event = new Event("test");
        boolean updated = event.updateListenerPriority("nonExistent", 10);
        assertFalse(updated);
    }

    @Test
    void testThreadSafeBreakLoop() throws Exception {
        Event event = new Event("concurrentTest");
        CountDownLatch latch = new CountDownLatch(2);

        event.addListener(new Listener() {
            @Override
            public void exec(Object object) {
                event.breakLoop();
            }
        });

        Thread t1 = new Thread(() -> {
            try {
                event.call(null);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                event.call(null);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        t1.start();
        t2.start();

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Both threads should complete");
    }

    @Test
    void testEmptyEventNameThrowsException() {
        assertThrows(EventException.class, () -> new Event(""));
    }

    @Test
    void testNullEventNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new Event(null));
    }

    @Test
    void testToString() {
        Event event = new Event("test");
        String str = event.toString();
        assertTrue(str.contains("test"));
    }

    @Test
    void testEquals() {
        Event event1 = new Event("same");
        Event event2 = new Event("same");
        assertEquals(event1, event2);
    }

    @Test
    void testHashCode() {
        Event event1 = new Event("same");
        Event event2 = new Event("same");
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}
