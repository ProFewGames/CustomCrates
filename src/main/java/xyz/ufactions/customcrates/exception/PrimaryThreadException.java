package xyz.ufactions.customcrates.exception;

/**
 * To be thrown when running a task on primary thread that shouldn't
 */
public class PrimaryThreadException extends CustomException {

    public PrimaryThreadException(String message) {
        super(message);
    }
}