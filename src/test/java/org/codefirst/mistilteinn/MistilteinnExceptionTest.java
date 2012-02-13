package org.codefirst.mistilteinn;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MistilteinnExceptionTest {
    @Test
    public void testGetCause() throws Exception {
        Exception innerException = new Exception();
        MistilteinnException mistilteinnException = new MistilteinnException(innerException);
        assertThat((Exception) mistilteinnException.getCause(), is(innerException));
    }

    @Test
    public void testGetMessage() throws Exception {
        String message = "exception message";
        Exception innerException = new Exception(message);
        MistilteinnException mistilteinnException = new MistilteinnException(innerException);
        assertThat(mistilteinnException.getMessage(), is(message));
    }

    @Test
    public void testGetMessageFromNullCause() throws Exception {
        MistilteinnException mistilteinnException = new MistilteinnException(null);
        assertThat(mistilteinnException.getMessage(), is(""));
    }

    @Test
    public void testGetLocalizedMessage() throws Exception {
        String message = "exception message";
        Exception innerException = new Exception(message);
        MistilteinnException mistilteinnException = new MistilteinnException(innerException);
        assertThat(mistilteinnException.getLocalizedMessage(), is(message));
    }
}
