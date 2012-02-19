package org.codefirst.mistilteinn.vcs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.codefirst.mistilteinn.MistilteinnException;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

public class GitAdapterTest {

    private Repository mockedRepository;
    private GitAdapter gitAdapter;
    private Git mockedGit;

    @Before
    public void setup() throws Exception {
        this.mockedRepository = mock(Repository.class);
        this.gitAdapter = spy(new GitAdapter(mockedRepository));
        this.mockedGit = mock(Git.class);
        doReturn(this.mockedGit).when(this.gitAdapter).getGit();
    }

    @Test
    public void testTicket() throws Exception {
        int ticketId = 100;
        String branchName = "id/" + ticketId;

        CheckoutCommand checkoutCommand = mock(CheckoutCommand.class);
        doReturn(checkoutCommand).when(mockedGit).checkout();

        doReturn(true).when(gitAdapter).branchExists(branchName);

        assertThat(gitAdapter.ticket(ticketId), is(true));

        verify(checkoutCommand).setName(branchName);
        verify(checkoutCommand).call();
    }

    @Test
    public void testTicketBranchNotExists() throws Exception {
        int ticketId = 100;
        String branchName = "id/" + ticketId;

        CheckoutCommand checkoutCommand = mock(CheckoutCommand.class);
        doReturn(checkoutCommand).when(mockedGit).checkout();

        doReturn(false).when(gitAdapter).branchExists(branchName);

        gitAdapter.ticket(ticketId);

        verify(checkoutCommand).setCreateBranch(true);
    }

    @Test
    public void testBranchExists() throws Exception {
        String branchName = "branch";

        ListBranchCommand mockedCommand = mock(ListBranchCommand.class);

        Ref mockedRef = mock(Ref.class);
        doReturn(branchName).when(mockedRef).getName();
        List<Ref> refs = new ArrayList<Ref>();
        refs.add(mockedRef);

        doReturn(mockedCommand).when(mockedGit).branchList();
        doReturn(refs).when(mockedCommand).call();

        assertThat(gitAdapter.branchExists(branchName), is(true));
    }

    @Test
    public void testBranchNotExists() throws Exception {
        String branchName = "branch";

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

    @Test(expected = MistilteinnException.class)
    public void testTicketThrowJGitInternalException() throws Exception {
        Exception exception = new JGitInternalException("");
        throwExceptionByTicketMethod(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testTicketThrowRefAlreadyExistsException() throws Exception {
        Exception exception = new RefAlreadyExistsException("");
        throwExceptionByTicketMethod(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testTicketThrowRefNotFoundException() throws Exception {
        Exception exception = new RefNotFoundException("");
        throwExceptionByTicketMethod(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testTicketThrowInvalidRefNameException() throws Exception {
        Exception exception = new InvalidRefNameException("");
        throwExceptionByTicketMethod(exception);
    }

    private void throwExceptionByTicketMethod(Exception exception) throws Exception {
        int ticketId = 1;
        String branchName = "id/" + ticketId;
        doReturn(true).when(gitAdapter).branchExists(branchName);
        CheckoutCommand mockedCheckoutCommand = mock(CheckoutCommand.class);
        doReturn(mockedCheckoutCommand).when(mockedGit).checkout();
        doThrow(exception).when(mockedCheckoutCommand).call();
        gitAdapter.ticket(ticketId);
    }

    @Test
    public void testGetNowMessage() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2012, 1, 1, 1, 3, 4);
        doReturn(new Date(cal.getTimeInMillis())).when(gitAdapter).getDate();

        String message = gitAdapter.getNowMessage();
        assertThat(message, is("[from now] 2012/02/01 01:03:04"));
    }

    @Test
    public void testNow() throws Exception {
        String nowMessage = "now message";

        doReturn(nowMessage).when(gitAdapter).getNowMessage();

        AddCommand mockedAddCommand = mock(AddCommand.class);
        doReturn(mockedAddCommand).when(mockedGit).add();
        CommitCommand mockedCommitCommand = mock(CommitCommand.class);
        doReturn(mockedCommitCommand).when(mockedGit).commit();

        gitAdapter.now();

        verify(mockedAddCommand).call();
        verify(mockedCommitCommand).call();
        verify(mockedCommitCommand).setMessage(nowMessage);
    }

    @Test(expected = MistilteinnException.class)
    public void testNowThrowNoFilepatternException() throws Exception {
        Exception exception = new NoFilepatternException("");
        doThrow(exception).when(gitAdapter).addAll();
        gitAdapter.now();
    }

    @Test(expected = MistilteinnException.class)
    public void testNowThrowNoHeadException() throws Exception {
        Exception exception = new NoHeadException("");
        throwExceptionByNowMethod(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testNowThrowNoMessageException() throws Exception {
        Exception exception = new NoHeadException("");
        throwExceptionByNowMethod(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testNowThrowUnmergedPathException() throws Exception {
        Exception exception = mock(UnmergedPathException.class);
        throwExceptionByNowMethod(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testNowThrowConcurrentRefUpdateException() throws Exception {
        Exception exception = mock(ConcurrentRefUpdateException.class);
        throwExceptionByNowMethod(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testNowThrowJGitInternalException() throws Exception {
        Exception exception = mock(JGitInternalException.class);
        throwExceptionByNowMethod(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testNowThrowWrongRepositoryStateException() throws Exception {
        Exception exception = mock(WrongRepositoryStateException.class);
        throwExceptionByNowMethod(exception);
    }

    private void throwExceptionByNowMethod(Exception exception) throws Exception {
        doThrow(exception).when(gitAdapter).commit(anyString());
        doNothing().when(gitAdapter).addAll();
        gitAdapter.now();
    }

    @Test
    public void testFixup() throws Exception {
        String commitMessage = "commit message";

        // mock log command
        RevCommit mockedCommit = mock(RevCommit.class);
        doReturn("[from now] 2011/01/23 01:23:45").when(gitAdapter).getShortMessage(mockedCommit);
        List<RevCommit> commits = new ArrayList<RevCommit>();
        commits.add(mockedCommit);

        LogCommand mockedLogCommand = mock(LogCommand.class);
        doReturn(mockedLogCommand).when(mockedGit).log();
        doReturn(commits).when(mockedLogCommand).call();

        doReturn(null).when(gitAdapter).resetTo(mockedCommit, ResetType.MIXED);

        // mock add command
        AddCommand mockedAddCommand = mock(AddCommand.class);
        doReturn(mockedAddCommand).when(mockedGit).add();

        // mock commit command
        CommitCommand mockedCommitCommand = mock(CommitCommand.class);
        doReturn(mockedCommitCommand).when(mockedGit).commit();

        gitAdapter.fixup(commitMessage);

        verify(mockedGit).add();
        verify(mockedGit).log();
        verify(mockedGit).commit();

        verify(mockedCommitCommand).setMessage(commitMessage);
    }

    @Test(expected = MistilteinnException.class)
    public void testFixupThrowNoHeadException() throws Exception {
        doThrow(new NoHeadException("")).when(gitAdapter).searchFirstNowCommit();
        gitAdapter.fixup("");
    }

    @Test(expected = MistilteinnException.class)
    public void testFixupThrowIOException() throws Exception {
        doReturn(mock(RevCommit.class)).when(gitAdapter).searchFirstNowCommit();
        doThrow(new IOException("")).when(gitAdapter).resetTo((RevCommit) anyObject(), (ResetType) anyObject());
        gitAdapter.fixup("");
    }

    @Test(expected = MistilteinnException.class)
    public void testFixupThrowNoFilepatternException() throws Exception {
        doReturn(mock(RevCommit.class)).when(gitAdapter).searchFirstNowCommit();
        doReturn(null).when(gitAdapter).resetTo((RevCommit) anyObject(), (ResetType) anyObject());
        doThrow(new NoFilepatternException("")).when(gitAdapter).addAll();
        gitAdapter.fixup("");
    }

    @Test(expected = MistilteinnException.class)
    public void testFixupThrowJGitInternalException() throws Exception {
        doReturn(mock(RevCommit.class)).when(gitAdapter).searchFirstNowCommit();
        doReturn(null).when(gitAdapter).resetTo((RevCommit) anyObject(), (ResetType) anyObject());
        doNothing().when(gitAdapter).addAll();
        doThrow(new JGitInternalException("")).when(gitAdapter).commit("");
        gitAdapter.fixup("");
    }

    @Test(expected = MistilteinnException.class)
    public void testFixupThrowNoMessageException() throws Exception {
        doReturn(mock(RevCommit.class)).when(gitAdapter).searchFirstNowCommit();
        doReturn(null).when(gitAdapter).resetTo((RevCommit) anyObject(), (ResetType) anyObject());
        doNothing().when(gitAdapter).addAll();
        doThrow(new NoMessageException("")).when(gitAdapter).commit("");
        gitAdapter.fixup("");
    }

    @Test(expected = MistilteinnException.class)
    public void testFixupThrowConcurrentRefUpdateException() throws Exception {
        doReturn(mock(RevCommit.class)).when(gitAdapter).searchFirstNowCommit();
        doReturn(null).when(gitAdapter).resetTo((RevCommit) anyObject(), (ResetType) anyObject());
        doNothing().when(gitAdapter).addAll();
        doThrow(new ConcurrentRefUpdateException("", null, null)).when(gitAdapter).commit("");
        gitAdapter.fixup("");
    }

    @Test(expected = MistilteinnException.class)
    public void testFixupThrowWrongRepositoryStateException() throws Exception {
        doReturn(mock(RevCommit.class)).when(gitAdapter).searchFirstNowCommit();
        doReturn(null).when(gitAdapter).resetTo((RevCommit) anyObject(), (ResetType) anyObject());
        doNothing().when(gitAdapter).addAll();
        doThrow(new WrongRepositoryStateException("")).when(gitAdapter).commit("");
        gitAdapter.fixup("");
    }

    @Test(expected = MistilteinnException.class)
    public void testMasterizeThrowNoHeadException() throws Exception {
        doThrow(new NoHeadException("")).when(gitAdapter).rebaseTo("master");
        gitAdapter.masterize();
    }

    @Test(expected = MistilteinnException.class)
    public void testMasterizeThrowRefNotFoundException() throws Exception {
        doThrow(new RefNotFoundException("")).when(gitAdapter).rebaseTo("master");
        gitAdapter.masterize();
    }

    @Test(expected = MistilteinnException.class)
    public void testMasterizeThrowGitAPIException() throws Exception {
        doThrow(mock(GitAPIException.class)).when(gitAdapter).rebaseTo("master");
        gitAdapter.masterize();
    }

    @Test(expected = MistilteinnException.class)
    public void testMasterizeThrowJGitInternalException() throws Exception {
        doThrow(mock(JGitInternalException.class)).when(gitAdapter).rebaseTo("master");
        gitAdapter.masterize();
    }

    @Test(expected = MistilteinnException.class)
    public void testMasterizeThrowIOException() throws Exception {
        RebaseResult mockedRebaseResult = mock(RebaseResult.class);
        doReturn(mockedRebaseResult).when(gitAdapter).rebaseTo("master");
        RevCommit mockedCommit = mock(RevCommit.class);
        doReturn(mockedCommit).when(mockedRebaseResult).getCurrentCommit();
        doNothing().when(gitAdapter).checkoutTo("master");
        doThrow(new IOException()).when(gitAdapter).resetTo((RevCommit) anyObject(), (ResetType) anyObject());
        gitAdapter.masterize();
    }

    @Test
    public void testAddAll() throws Exception {
        GitAdapter gitAdapter = spy(new GitAdapter(mock(Repository.class)));
        Git mockedGit = mock(Git.class);
        doReturn(mockedGit).when(gitAdapter).getGit();

        AddCommand mockedAddCommand = mock(AddCommand.class);
        doReturn(mockedAddCommand).when(mockedGit).add();

        gitAdapter.addAll();

        verify(mockedAddCommand).addFilepattern(".");
        verify(mockedAddCommand).call();
    }

    @Test
    public void testResetTo() throws Exception {
        ResetType resetType = ResetType.MIXED;

        // mock reset command
        ResetCommand mockedResetCommand = mock(ResetCommand.class);
        doReturn(mockedResetCommand).when(mockedGit).reset();

        RevCommit commit = mock(RevCommit.class);

        gitAdapter.resetTo(commit, resetType);

        verify(mockedResetCommand).setMode(resetType);
        verify(mockedResetCommand).call();
    }

    @Test
    public void testRebaseTo() throws Exception {
        String branchName = "branch";
        RebaseCommand mockedRebaseCommand = mock(RebaseCommand.class);
        doReturn(mockedRebaseCommand).when(mockedGit).rebase();
        gitAdapter.rebaseTo(branchName);

        verify(mockedRebaseCommand).setUpstream("branch");
        verify(mockedRebaseCommand).call();
    }

    @Test
    public void testCheckoutTo() throws Exception {
        String branchName = "branch";
        CheckoutCommand mockedCheckoutCommand = mock(CheckoutCommand.class);
        doReturn(mockedCheckoutCommand).when(mockedGit).checkout();
        gitAdapter.checkoutTo(branchName);

        verify(mockedCheckoutCommand).setName(branchName);
        verify(mockedCheckoutCommand).call();
    }

    @Test
    public void testCommit() throws Exception {
        String message = "message";
        CommitCommand mockedCommitCommand = mock(CommitCommand.class);
        doReturn(mockedCommitCommand).when(mockedGit).commit();

        gitAdapter.commit(message);

        verify(mockedCommitCommand).setMessage(message);
        verify(mockedCommitCommand).call();
    }

    @Test
    public void testGetDirectory() throws Exception {
        File file = new File(".");
        doReturn(file).when(mockedRepository).getDirectory();
        File result = gitAdapter.getDirectory();
        assertThat(result, is(file));
    }

}
