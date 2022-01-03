package xyz.ufactions.customcrates.item.exception;

public class ItemReadException extends Exception {

    public ItemReadException() {
    }

    public ItemReadException(String message) {
        super(message);
    }

    public ItemReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemReadException(Throwable cause) {
        super(cause);
    }

    public ItemReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}