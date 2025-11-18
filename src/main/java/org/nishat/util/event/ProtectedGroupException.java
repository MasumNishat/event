package org.nishat.util.event;

/**
 * Exception thrown when attempting to modify a protected event group.
 * Protected groups cannot have events removed from them.
 */
public class ProtectedGroupException extends EventException {

    /**
     * Constructs a new protected group exception for the specified group name.
     *
     * @param groupName the name of the protected group that cannot be modified
     */
    public ProtectedGroupException(String groupName) {
        super("Cannot modify protected group: " + groupName);
    }
}
