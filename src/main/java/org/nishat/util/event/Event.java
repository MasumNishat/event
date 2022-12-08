package org.nishat.util.event;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
        listeners.put(listener.getId(), listener);
    }

    /**
     * Remove a {@link Listener} to this {@link Event}.
     * @param listener {@link Listener}
     */
    public void removeListener(Listener listener) {
        Objects.requireNonNull(listener);
        listeners.remove(listener.getId());
    }

    /**
     * Remove a {@link Listener} to this {@link Event}.
     * @param id {@link String}
     */
    public void removeListener(String id) {
        listeners.remove(id);
    }

    /**
     * Call all {@link Listener}s of this {@link Event}.
     * @param payload {@link Object}
     * @throws Throwable if {@link Listener} throws error from implementation
     */
    public void call(Object payload) throws Throwable {
        synchronized (this) {
            ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<>(this.listeners);
            for (Listener listener : listeners.values()) {
                listener.exec(payload);
                if (breakLoop) {
                    breakLoop = false;
                    break;
                }
            }
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
}
