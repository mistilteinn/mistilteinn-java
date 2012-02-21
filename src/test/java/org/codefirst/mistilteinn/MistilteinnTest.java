package org.codefirst.mistilteinn;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.codefirst.mistilteinn.its.ITS;
import org.codefirst.mistilteinn.its.Ticket;
import org.codefirst.mistilteinn.vcs.GitAdapter;
import org.junit.Test;

public class MistilteinnTest {
    @Test
    public void testTicket() throws Exception {
        int ticketId = 100;
        GitAdapter mockedGitAdapter = mock(GitAdapter.class);
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        doReturn(mockedGitAdapter).when(mistilteinn).getVCSAdapter();
        ITS mockedITS = mock(ITS.class);
        doReturn(new Ticket(Integer.valueOf(ticketId), "subject")).when(mockedITS).getTicket(ticketId);
        doReturn(mockedITS).when(mistilteinn).getITS();

        mistilteinn.ticket(100);

        verify(mockedGitAdapter).ticket(100);
    }

    @Test
    public void testMasterize() throws Exception {
        GitAdapter mockedGitAdapter = mock(GitAdapter.class);
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        doReturn(mockedGitAdapter).when(mistilteinn).getVCSAdapter();

        mistilteinn.masterize();

        verify(mockedGitAdapter).masterize("master");
    }

    @Test
    public void testMasterizeWithBranchName() throws Exception {
        String branchName = "branch";
        GitAdapter mockedGitAdapter = mock(GitAdapter.class);
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        doReturn(mockedGitAdapter).when(mistilteinn).getVCSAdapter();

        mistilteinn.masterize(branchName);

        verify(mockedGitAdapter).masterize(branchName);
    }
}
