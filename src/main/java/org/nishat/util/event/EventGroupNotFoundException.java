package org.nishat.util.event;

/**
 * Exception thrown when an event group is not found.
 * This exception is thrown when attempting to access or manipulate a group that doesn't exist.
 */
public class EventGroupNotFoundException extends EventException {

    /**
     * Constructs a new event group not found exception for the specified group name.
     *
     * @param groupName the name of the group that was not found
     */
    public EventGroupNotFoundException(String groupName) {
        super("Event group not found: " + groupName);
    }
}
