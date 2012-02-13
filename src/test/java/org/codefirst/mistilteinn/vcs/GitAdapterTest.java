package org.codefirst.mistilteinn.vcs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.junit.Test;

public class GitAdapterTest {

    @Test
    public void testTicket() throws Exception {
        int ticketId = 100;

        Repository mockedRepository = mock(Repository.class);
        GitAdapter gitAdapter = spy(new GitAdapter(mockedRepository));
        Git mockedGit = mock(Git.class);
        doReturn(mockedGit).when(gitAdapter).getGit();
        CheckoutCommand checkoutCommand = mock(CheckoutCommand.class);
        doReturn(checkoutCommand).when(mockedGit).checkout();

        assertThat(gitAdapter.ticket(ticketId), is(true));

        verify(checkoutCommand).setName("id/" + ticketId);
        verify(checkoutCommand).call();
    }
}
