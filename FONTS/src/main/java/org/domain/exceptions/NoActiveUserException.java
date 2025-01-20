package org.domain.exceptions;

/**
 * Exception thrown when there is no active user in the system.
 */
public class NoActiveUserException extends Exception {
    public NoActiveUserException() {
        super("No active user in the system.");
    }
}