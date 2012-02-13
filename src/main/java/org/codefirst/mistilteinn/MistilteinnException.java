package org.codefirst.mistilteinn;

import org.apache.commons.lang.StringUtils;

/**
 * Exception wrappter from dependent libraries.
 */
public class MistilteinnException extends Exception {
    /** generated serial UID */
    private static final long serialVersionUID = 7663446248122692219L;

    /**
     * constructor with arguments.
     * @param cause cause of exception
     */
    public MistilteinnException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        String message = StringUtils.EMPTY;
        if (this.getCause() != null) {
            message = this.getCause().getMessage();
        }
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return this.getMessage();
    }
}
