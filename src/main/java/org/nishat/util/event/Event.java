package org.nishat.util.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * {@link Event} is an {@link Event} to run {@link Listener}s. Example:
 * <pre>
 *        //create event using builder
 *         Event ev = Event.builder("foo")
 *                 .withExceptionHandling(true)
 *                 .build();
 *
 *         //register {@link Listener}s
 *         ev.addListener(new Listener() {
 *             {@literal @}Override
 *             public void exec(Object object) throws Throwable {
 *                 System.out.println("listener 1");
 *             }
 *         });
 *
 *         //{@link Listener} with custom id and priority
 *         ev.addListener(new Listener("listenerFoo") {
 *             {@literal @}Override
 *             public void exec(Object object) throws Throwable {
 *                 System.out.println("listener 2");
 *                 //break loop (prevent execution of next listener)
 *                 ev.breakLoop();
 *             }
 *         }, 10); // Higher priority
 *
 *         //call
 *         try {
 *             ev.call(null);
 *         } catch (Throwable e) {
 *             e.printStackTrace();
 *         }
 *
 *         //async call
 *         CompletableFuture&lt;Void&gt; future = ev.callAsync(null);
 *         future.thenRun(() -> System.out.println("Async execution completed"));
 * </pre>
 */
public final class Event {
    private static final Logger logger = LoggerFactory.getLogger(Event.class);
    private static final int DEFAULT_PRIORITY = 0;

    private final String name;
    private final ConcurrentHashMap<String, PrioritizedListener> listeners = new ConcurrentHashMap<>();
    private final ThreadLocal<AtomicBoolean> breakLoop = ThreadLocal.withInitial(AtomicBoolean::new);
    private final List<String> groupName = Collections.synchronizedList(new ArrayList<>());
    private final ReentrantLock callLock = new ReentrantLock();
    private final ExecutorService asyncExecutor;
    private final boolean handleExceptions;

    private volatile Consumer<Event> doAfterEachCall;
    private volatile Consumer<Event> doBeforeEachCall;
    private volatile Consumer<Listener> doAfterEachListenerCall;
    private volatile Consumer<Listener> doBeforeEachListenerCall;

    /**
     * Create new {@link Event} with default configuration
     * @param name {@link String}
     */
    public Event(String name) {
        this(name, null, false);
    }

    private Event(String name, ExecutorService executor, boolean handleExceptions) {
        Objects.requireNonNull(name, "Event name cannot be null");
        if (name.trim().isEmpty()) {
            throw new EventException("Event name cannot be empty");
        }
        this.name = name;
        this.asyncExecutor = executor != null ? executor : ForkJoinPool.commonPool();
        this.handleExceptions = handleExceptions;
    }

    /**
     * Create a builder for this Event
     * @param name {@link String} event name
     * @return {@link EventBuilder}
     */
    public static EventBuilder builder(String name) {
        return new EventBuilder(name);
    }

    /**
     * Builder pattern for Event creation
     */
    public static class EventBuilder {
        private final String name;
        private ExecutorService executor;
        private boolean handleExceptions = false;

        private EventBuilder(String name) {
            this.name = name;
        }

        /**
         * Set custom executor for async operations
         * @param executor {@link ExecutorService}
         * @return {@link EventBuilder}
         */
        public EventBuilder withExecutor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Enable exception handling (continues executing listeners even if one fails)
         * @param handleExceptions boolean
         * @return {@link EventBuilder}
         */
        public EventBuilder withExceptionHandling(boolean handleExceptions) {
            this.handleExceptions = handleExceptions;
            return this;
        }

        /**
         * Build the Event
         * @return {@link Event}
         */
        public Event build() {
            return new Event(name, executor, handleExceptions);
        }
    }

    /**
     * Get name of {@link Event}
     * @return {@link String}
     */
    public String name() {
        return name;
    }

    /**
     * Add a {@link Listener} to this {@link Event} with default priority.
     * @param listener {@link Listener}
     */
    public void addListener(Listener listener) {
        addListener(listener, DEFAULT_PRIORITY);
    }

    /**
     * Add a {@link Listener} to this {@link Event} with specified priority.
     * Higher priority listeners execute first.
     * @param listener {@link Listener}
     * @param priority int (higher value = higher priority)
     */
    public void addListener(Listener listener, int priority) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        logger.debug("Registering Listener: {} [Event: {}] [Priority: {}]", listener.getId(), name(), priority);
        listeners.put(listener.getId(), new PrioritizedListener(listener, priority));
        logger.info("Registered Listener: {} [Event: {}]", listener.getId(), name());
    }

    /**
     * Remove a {@link Listener} from this {@link Event}.
     * @param listener {@link Listener}
     */
    public void removeListener(Listener listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        removeListener(listener.getId());
    }

    /**
     * Remove a {@link Listener} from this {@link Event}.
     * @param id {@link String}
     */
    public void removeListener(String id) {
        Objects.requireNonNull(id, "Listener ID cannot be null");
        logger.debug("Removing Listener: {} [Event: {}]", id, name());
        listeners.remove(id);
        logger.info("Removed Listener: {} [Event: {}]", id, name());
    }

    /**
     * Call all {@link Listener}s of this {@link Event} synchronously.
     * @param payload {@link Object}
     * @throws Throwable if {@link Listener} throws error and exception handling is disabled
     */
    public void call(Object payload) throws Throwable {
        callLock.lock();
        try {
            breakLoop.get().set(false);

            if (this.doBeforeEachCall != null) {
                this.doBeforeEachCall.accept(this);
            }

            List<PrioritizedListener> sortedListeners = listeners.values().stream()
                    .sorted(Comparator.comparingInt(PrioritizedListener::getPriority).reversed())
                    .collect(Collectors.toList());

            List<ListenerExecutionException> exceptions = new ArrayList<>();

            for (PrioritizedListener prioritizedListener : sortedListeners) {
                Listener listener = prioritizedListener.getListener();

                try {
                    logger.debug("Calling Listener: {} [Event: {}]", listener.getId(), name());

                    if (this.doBeforeEachListenerCall != null) {
                        this.doBeforeEachListenerCall.accept(listener);
                    }

                    listener.doBeforeCall();
                    listener.exec(payload);
                    listener.doAfterCall();

                    if (this.doAfterEachListenerCall != null) {
                        this.doAfterEachListenerCall.accept(listener);
                    }

                    logger.debug("Called Listener: {} [Event: {}]", listener.getId(), name());
                } catch (Throwable t) {
                    ListenerExecutionException exception = new ListenerExecutionException(
                            listener.getId(), name(), t);

                    logger.error("Listener execution failed: {} [Event: {}]", listener.getId(), name(), t);

                    if (handleExceptions) {
                        exceptions.add(exception);
                    } else {
                        throw exception;
                    }
                }

                if (breakLoop.get().get()) {
                    logger.debug("Loop broken at Listener: {} [Event: {}]", listener.getId(), name());
                    breakLoop.get().set(false);
                    break;
                }
            }

            if (this.doAfterEachCall != null) {
                this.doAfterEachCall.accept(this);
            }

            // If exception handling is enabled and there were exceptions, log them
            if (handleExceptions && !exceptions.isEmpty()) {
                logger.warn("Event {} completed with {} listener failures", name(), exceptions.size());
            }
        } finally {
            callLock.unlock();
        }
    }

    /**
     * Call all {@link Listener}s of this {@link Event} asynchronously.
     * @param payload {@link Object}
     * @return {@link CompletableFuture}&lt;Void&gt;
     */
    public CompletableFuture<Void> callAsync(Object payload) {
        return CompletableFuture.runAsync(() -> {
            try {
                call(payload);
            } catch (Throwable t) {
                logger.error("Async event call failed: {}", name(), t);
                throw new CompletionException(t);
            }
        }, asyncExecutor);
    }

    /**
     * Cancel execution of other {@link Listener}s after running {@link Listener}.
     * Thread-safe using ThreadLocal to support concurrent event calls.
     */
    public void breakLoop() {
        breakLoop.get().set(true);
    }

    /**
     * Check if a {@link Listener} exists
     * @param id {@link String}
     * @return boolean
     */
    public boolean hasListener(String id) {
        return listeners.containsKey(id);
    }

    /**
     * Check if a {@link Listener} exists
     * @param listener {@link Listener}
     * @return boolean
     */
    public boolean hasListener(Listener listener) {
        return listeners.containsKey(listener.getId());
    }

    /**
     * Get the priority of a listener
     * @param listenerId {@link String}
     * @return {@link Optional}&lt;Integer&gt; priority, empty if listener not found
     */
    public Optional<Integer> getListenerPriority(String listenerId) {
        PrioritizedListener pl = listeners.get(listenerId);
        return pl != null ? Optional.of(pl.getPriority()) : Optional.empty();
    }

    /**
     * Update the priority of an existing listener
     * @param listenerId {@link String}
     * @param newPriority int
     * @return boolean true if updated, false if listener not found
     */
    public boolean updateListenerPriority(String listenerId, int newPriority) {
        PrioritizedListener pl = listeners.get(listenerId);
        if (pl != null) {
            listeners.put(listenerId, new PrioritizedListener(pl.getListener(), newPriority));
            logger.debug("Updated priority for Listener: {} [Event: {}] to {}", listenerId, name(), newPriority);
            return true;
        }
        return false;
    }

    Event setGroupName(String groupName) {
        Objects.requireNonNull(groupName, "Group name cannot be null");
        if (groupName.trim().isEmpty()) {
            throw new EventException("Group name should not be empty");
        }
        this.groupName.add(groupName);
        return this;
    }

    /**
     * Gets the names of all groups this event belongs to.
     * The returned list is unmodifiable.
     *
     * @return an unmodifiable list of group names
     */
    public List<String> getGroupNames() {
        return Collections.unmodifiableList(new ArrayList<>(groupName));
    }

    /**
     * Removing group from {@link Event} will not unregister event from respective group.
     * It is required to unregister from group also.
     * @param groupName {@link String}
     */
    void removeGroup(String groupName) {
        Objects.requireNonNull(groupName, "Group name cannot be null");
        if (groupName.trim().isEmpty()) {
            throw new EventException("Group name should not be empty");
        }
        this.groupName.remove(groupName);
    }

    /**
     * Execute a function after each time calling all {@link Listener}.
     * It is not dependent on number of {@link Listener} registered.
     * @param function {@link Consumer}&lt;{@link Event}&gt;
     */
    public void doAfterEachCall(Consumer<Event> function) {
        this.doAfterEachCall = function;
    }

    /**
     * Execute a function before each time calling all {@link Listener}.
     * It is not dependent on number of {@link Listener} registered.
     * @param function {@link Consumer}&lt;{@link Event}&gt;
     */
    public void doBeforeEachCall(Consumer<Event> function) {
        this.doBeforeEachCall = function;
    }

    /**
     * Execute a function after calling every single {@link Listener}
     * @param function {@link Consumer}&lt;{@link Listener}&gt;
     */
    public void doAfterEachListenerCall(Consumer<Listener> function) {
        this.doAfterEachListenerCall = function;
    }

    /**
     * Execute a function before calling every single {@link Listener}
     * @param function {@link Consumer}&lt;{@link Listener}&gt;
     */
    public void doBeforeEachListenerCall(Consumer<Listener> function) {
        this.doBeforeEachListenerCall = function;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", listeners=" + listeners.size() +
                ", groups=" + groupName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Internal class to hold listener with priority
     */
    private static class PrioritizedListener {
        private final Listener listener;
        private final int priority;

        public PrioritizedListener(Listener listener, int priority) {
            this.listener = listener;
            this.priority = priority;
        }

        public Listener getListener() {
            return listener;
        }

        public int getPriority() {
            return priority;
        }
    }
}
