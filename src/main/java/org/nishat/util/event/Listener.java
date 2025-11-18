package org.nishat.util.event;

import java.util.Objects;
import java.util.UUID;

/**
 * {@link Listener} is a {@link Listener} to run with {@link Event} call. Example:
 * <pre>
 *        //create new listener
 *         Listener listener = new Listener("listenerFoo") {
 *             {@literal @}Override
 *             public void exec(Object object) throws Throwable {
 *                 System.out.println("listener called");
 *             }
 *         };
 *         //get id
 *         String id = listener.getId();
 * </pre>
 */
public abstract class Listener {
    private final String id;

    /**
     * create new {@link Listener} with default UUID id
     */
    public Listener() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * create new {@link Listener} with supplied id
     * @param id {@link String}
     * @throws EventException if id is null or empty
     */
    public Listener(String id) {
        Objects.requireNonNull(id, "Listener ID cannot be null");
        if (id.trim().isEmpty()) {
            throw new EventException("Listener ID cannot be empty");
        }
        this.id = id;
    }

    /**
     * code to execute when associated {@link Event} will call
     * @param object {@link Object}
     * @throws Throwable if required by the implementation
     */
    public abstract void exec(Object object) throws Throwable;

    /**
     * get id of this {@link Listener}
     * @return {@link String}
     */
    public final String getId() {
        return id;
    }

    /**
     * Execute after calling {@link Listener}
     */
    public void doAfterCall() {}

    /**
     * Execute before calling {@link Listener}
     */
    public void doBeforeCall() {}

    @Override
    public String toString() {
        return "Listener{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Listener)) return false;
        Listener listener = (Listener) o;
        return Objects.equals(id, listener.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
