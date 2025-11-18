package org.nishat.util.event;

/**
 * Exception thrown when attempting to modify a protected event group.
 */
public class ProtectedGroupException extends EventException {

    public ProtectedGroupException(String groupName) {
        super("Cannot modify protected group: " + groupName);
    }
}
