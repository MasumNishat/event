package org.nishat.util.event;

import org.nishat.util.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link Event} is an  {@link Event} to run {@link Listener}s. Example:
 * <pre>
 *        //create event
 *         Event ev = new Event("foo");
 *         //register {@link Listener}s
 *         ev.addListener(new Listener() {
 *             {@literal @}Override
 *             public void exec(Object object) throws Throwable {
 *                 System.out.println("listener 1");
 *             }
 *         });
 *         //{@link Listener} with custom id
 *         ev.addListener(new Listener("listenerFoo") {
 *             {@literal @}Override
 *             public void exec(Object object) throws Throwable {
 *                 System.out.println("listener 2");
 *                 //break loop (prevent execution of next listener)
 *                 ev.breakLoop();
 *             }
 *         });
 *         ev.addListener(new Listener("listenerFoo1") {
 *             {@literal @}Override
 *             public void exec(Object object) throws Throwable {
 *                 System.out.println("listener 3");
 *             }
 *         });
 *         //call
 *         try {
 *             ev.call(null);
 *         } catch (Throwable e) {
 *             e.printStackTrace();
 *         }
 *         //check if a listener exist
 *         if (ev.hasListener("listenerFoo1")) {
 *             System.out.println("found");
 *         }
 *         ////////output////////
 *         listener 1
 *         listener 2
 *         found
 * </pre>
 */
public final class Event {
    private final String name;
    private final ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<>();
    private boolean breakLoop = false;
    private List<String> groupName = Collections.synchronizedList(new ArrayList<>());
    private Consumer<Event> doAfterEachCall;
    private Consumer<Event> doBeforeEachCall;
    private Consumer<Listener> doAfterEachListenerCall;
    private Consumer<Listener> doBeforeEachListenerCall;

    /**
     * Create new {@link Event}
     * @param name {@link String}
     */
    public Event(String name) {
        this.name = name;
    }

    /**
     * Get name of {@link Event}
     * @return {@link String}
     */
    public String name(){
        return name;
    }

    /**
     * Add a {@link Listener} to this {@link Event}.
     * @param listener {@link Listener}
     */
    public void addListener(Listener listener) {
        Objects.requireNonNull(listener);
        Log.i("Registering Listener",listener.getId()+"[Event:"+name()+"]]");
        listeners.put(listener.getId(), listener);
        Log.i("Registered Listener",listener.getId()+"[Event:"+name()+"]]");
    }

    /**
     * Remove a {@link Listener} to this {@link Event}.
     * @param listener {@link Listener}
     */
    public void removeListener(Listener listener) {
        Objects.requireNonNull(listener);
        Log.i("Removing Listener",listener.getId()+"[Event:"+name()+"]]");
        listeners.remove(listener.getId());
        Log.i("Removed Listener",listener.getId()+"[Event:"+name()+"]]");
    }

    /**
     * Remove a {@link Listener} to this {@link Event}.
     * @param id {@link String}
     */
    public void removeListener(String id) {
        Log.i("Removing Listener",id+"[Event:"+name()+"]]");
        listeners.remove(id);
        Log.i("Removed Listener",id+"[Event:"+name()+"]]");
    }

    /**
     * Call all {@link Listener}s of this {@link Event}.
     * @param payload {@link Object}
     * @throws Throwable if {@link Listener} throws error from implementation
     */
    public void call(Object payload) throws Throwable {
        synchronized (this) {
            if (this.doBeforeEachCall != null) this.doBeforeEachCall.accept(this);
            ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<>(this.listeners);
            for (Listener listener : listeners.values()) {
                Log.i("Calling Listener",listener.getId()+"[Event:"+name()+"]]");
                if (this.doBeforeEachListenerCall != null) this.doBeforeEachListenerCall.accept(listener);
                listener.doBeforeCall();
                listener.exec(payload);
                listener.doAfterCall();
                if (this.doAfterEachListenerCall != null) this.doAfterEachListenerCall.accept(listener);
                Log.i("Called Listener",listener.getId()+"[Event:"+name()+"]]");
                if (breakLoop) {
                    breakLoop = false;
                    break;
                }
            }
            if (this.doAfterEachCall != null) this.doAfterEachCall.accept(this);
        }
    }

    /**
     * Cancel execution of other {@link Listener}s after running {@link Listener}.
     * <pre>
     *        //create event
     *         Event ev = new Event("foo");
     *         //register {@link Listener}s
     *         ev.addListener(new Listener() {
     *             {@literal @}Override
     *             public void exec(Object object) throws Throwable {
     *                 System.out.println("listener 1");
     *             }
     *         });
     *         ev.addListener(new Listener() {
     *             {@literal @}Override
     *             public void exec(Object object) throws Throwable {
     *                 System.out.println("listener 2");
     *                 ev.breakLoop();
     *             }
     *         });
     *         ev.addListener(new Listener() {
     *             {@literal @}Override
     *             public void exec(Object object) throws Throwable {
     *                 System.out.println("listener 3");
     *             }
     *         });
     *         //call
     *         try {
     *             ev.call(null);
     *         } catch (Throwable e) {
     *             e.printStackTrace();
     *         }
     *         ////////output////////
     *         //listener 1
     *         //listener 2
     * </pre>
     */
    public void breakLoop() {
        breakLoop = true;
    }

    /**
     * Check if a {@link Listener} exist
     * @param id {@link String}
     * @return boolean
     */
    public boolean hasListener(String id) {
        return listeners.containsKey(id);
    }

    /**
     * Check if a {@link Listener} exist
     * @param listener {@link Listener}
     * @return boolean
     */
    public boolean hasListener(Listener listener) {
        return listeners.containsKey(listener.getId());
    }

    public Event setGroupName(String groupName) {
        Objects.requireNonNull(groupName);
        if (groupName.trim().equals("")) throw new RuntimeException("Group name should not be empty");
        this.groupName.add(groupName);
        return this;
    }

    public List<String> getGroupNames() {
        return Collections.synchronizedList(groupName);
    }

    /**
     * Removing group from {@link Event} will not unregister event from respective group. It is required to unregister from group also.
     * @param groupName {@link String}
     */
    public void removeGroup(String groupName) {
        Objects.requireNonNull(groupName);
        if (groupName.trim().equals("")) throw new RuntimeException("Group name should not be empty");
        this.groupName.remove(groupName);
    }

    /**
     * Execute a function after each time calling all {@link Listener}. It is not dependent on number of {@link Listener} registered.
     * @param function {@link Consumer}&lt;{@link Event}&gt;
     */
    public void doAfterEachCall(Consumer<Event> function) {
        this.doAfterEachCall = function;
    }

    /**
     * Execute a function before each time calling all {@link Listener}. It is not dependent on number of {@link Listener} registered.
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
}
