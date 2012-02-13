package org.codefirst.mistilteinn;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.codefirst.mistilteinn.vcs.GitAdapter;
import org.junit.Test;

public class MistilteinnTest {
    @Test
    public void testTicket() throws Exception {
        GitAdapter mockedGitAdapter = mock(GitAdapter.class);
        Mistilteinn mistilteinn = spy(new Mistilteinn(mockedGitAdapter));
        doReturn(mockedGitAdapter).when(mistilteinn).getVCSAdapter();

        mistilteinn.ticket(100);

        verify(mockedGitAdapter).ticket(100);
    }
}
