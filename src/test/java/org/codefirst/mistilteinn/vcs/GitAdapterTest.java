package org.codefirst.mistilteinn.vcs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
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

        doReturn(true).when(gitAdapter).branchExists("id/" + ticketId);

        assertThat(gitAdapter.ticket(ticketId), is(true));

        verify(checkoutCommand).setName("id/" + ticketId);
        verify(checkoutCommand).call();
    }

    @Test
    public void testBranchExists() throws Exception {
        GitAdapter gitAdapter = spy(new GitAdapter(mock(Repository.class)));
        String branchName = "branch";

        Git mockedGit = mock(Git.class);

        ListBranchCommand mockedCommand = mock(ListBranchCommand.class);

        Ref mockedRef = mock(Ref.class);
        doReturn(branchName).when(mockedRef).getName();
        List<Ref> refs = new ArrayList<Ref>();
        refs.add(mockedRef);

        doReturn(mockedGit).when(gitAdapter).getGit();
        doReturn(mockedCommand).when(mockedGit).branchList();
        doReturn(refs).when(mockedCommand).call();

        assertThat(gitAdapter.branchExists(branchName), is(true));
    }

    @Test
    public void testBranchNotExists() throws Exception {
        GitAdapter gitAdapter = spy(new GitAdapter(mock(Repository.class)));
        String branchName = "branch";

        Git mockedGit = mock(Git.class);

        ListBranchCommand mockedCommand = mock(ListBranchCommand.class);

        Ref mockedRef = mock(Ref.class);
        doReturn(branchName).when(mockedRef).getName();
        List<Ref> refs = new ArrayList<Ref>();
        refs.add(mockedRef);

        doReturn(mockedGit).when(gitAdapter).getGit();
        doReturn(mockedCommand).when(mockedGit).branchList();
        doReturn(refs).when(mockedCommand).call();

        assertThat(gitAdapter.branchExists(branchName + "a"), is(false));
    }
}
