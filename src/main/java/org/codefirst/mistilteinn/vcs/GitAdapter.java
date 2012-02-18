package org.codefirst.mistilteinn.vcs;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codefirst.mistilteinn.MistilteinnException;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
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
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Git Repository Adapter
 */
public class GitAdapter {

    /** target repository. */
    private Repository repository;

    /**
     * constructor.
     * @param gitPath path to repository (.git)
     * @throws IOException
     */
    public GitAdapter(String gitPath) throws IOException {
        this(new FileRepositoryBuilder().setGitDir(new File(gitPath)).readEnvironment().findGitDir().build());
    }

    /**
     * constructor.
     * @param repository target repository
     */
    public GitAdapter(Repository repository) {
        this.repository = repository;
    }

    /**
     * change branch to id/#{ticketId}.
     * @param ticketId ticket id
     * @return true if success
     * @throws MistilteinnException exception
     */
    public boolean ticket(int ticketId) throws MistilteinnException {
        CheckoutCommand checkoutCommand = getGit().checkout();
        checkoutCommand.setForce(true);
        String branchName = "id/" + ticketId;
        checkoutCommand.setName(branchName);
        if (!branchExists(branchName)) {
            checkoutCommand.setCreateBranch(true);
        }
        try {
            checkoutCommand.call();
        } catch (JGitInternalException e) {
            throw new MistilteinnException(e);
        } catch (RefAlreadyExistsException e) {
            throw new MistilteinnException(e);
        } catch (RefNotFoundException e) {
            throw new MistilteinnException(e);
        } catch (InvalidRefNameException e) {
            throw new MistilteinnException(e);
        }
        return true;
    }

    /**
     * check whether branch exists or not.
     * @param branchName branch name
     * @return true if branch exists
     */
    protected boolean branchExists(String branchName) {
        boolean exists = false;
        ListBranchCommand branchList = getGit().branchList();
        List<Ref> refList = branchList.call();
        for (Ref ref : refList) {
            if (StringUtils.equals(ref.getName(), branchName)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    /**
     * commit temporary
     * @throws MistilteinnException exception
     */
    public void now() throws MistilteinnException {
        try {
            addAll();
            commit(getNowMessage());
        } catch (NoFilepatternException e) {
            throw new MistilteinnException(e);
        } catch (NoHeadException e) {
            throw new MistilteinnException(e);
        } catch (NoMessageException e) {
            throw new MistilteinnException(e);
        } catch (UnmergedPathException e) {
            throw new MistilteinnException(e);
        } catch (ConcurrentRefUpdateException e) {
            throw new MistilteinnException(e);
        } catch (JGitInternalException e) {
            throw new MistilteinnException(e);
        } catch (WrongRepositoryStateException e) {
            throw new MistilteinnException(e);
        }
    }

    /**
     * commit with message
     * @param message message
     * @throws NoHeadException
     * @throws NoMessageException
     * @throws UnmergedPathException
     * @throws ConcurrentRefUpdateException
     * @throws WrongRepositoryStateException
     */
    protected void commit(String message) throws NoHeadException, NoMessageException, UnmergedPathException, ConcurrentRefUpdateException, WrongRepositoryStateException {
        CommitCommand commitCommand = getGit().commit();
        commitCommand.setAll(true);
        commitCommand.setMessage(message);
        commitCommand.call();
    }

    /**
     * fixup now commits.
     * @param message message
     * @throws MistilteinnException exception
     */
    public void fixup(String message) throws MistilteinnException {
        try {
            // search a first now commit
            RevCommit firstNowCommit = searchFirstNowCommit();

            if (firstNowCommit == null) {
                return;
            }

            resetTo(firstNowCommit, ResetType.MIXED);
            addAll();
            commit(message);
        } catch (NoHeadException e) {
            throw new MistilteinnException(e);
        } catch (JGitInternalException e) {
            throw new MistilteinnException(e);
        } catch (IOException e) {
            throw new MistilteinnException(e);
        } catch (NoFilepatternException e) {
            throw new MistilteinnException(e);
        } catch (NoMessageException e) {
            throw new MistilteinnException(e);
        } catch (ConcurrentRefUpdateException e) {
            throw new MistilteinnException(e);
        } catch (WrongRepositoryStateException e) {
            throw new MistilteinnException(e);
        }
    }

    /**
     * search first now commit.
     * @return found commit. (null if not found)
     * @throws NoHeadException
     */
    protected RevCommit searchFirstNowCommit() throws NoHeadException {
        RevCommit firstNowCommit = null;
        for (RevCommit commit : getGit().log().call()) {
            firstNowCommit = commit;
            if (!StringUtils.contains(getShortMessage(commit), "[from now]")) {
                break;
            }
        }
        return firstNowCommit;
    }

    /**
     * rebase to master branch.
     */
    public void masterize() throws MistilteinnException {
        try {
            RebaseResult rebaseResult = rebaseTo("master");
            RevCommit currentCommit = rebaseResult.getCurrentCommit();
            checkoutTo("master");
            resetTo(currentCommit, ResetType.HARD);
        } catch (NoWorkTreeException e) {
            throw new MistilteinnException(e);
        } catch (RefNotFoundException e) {
            throw new MistilteinnException(e);
        } catch (NoHeadException e) {
            throw new MistilteinnException(e);
        } catch (JGitInternalException e) {
            throw new MistilteinnException(e);
        } catch (GitAPIException e) {
            throw new MistilteinnException(e);
        } catch (IOException e) {
            throw new MistilteinnException(e);
        }
    }

    /**
     * rebase to branch
     * @param branchName branch name
     * @return rebase result
     * @throws RefNotFoundException
     * @throws NoHeadException
     * @throws GitAPIException
     */
    protected RebaseResult rebaseTo(String branchName) throws RefNotFoundException, NoHeadException, GitAPIException {
        RebaseCommand rebaseCommand = getGit().rebase();
        rebaseCommand.setUpstream(branchName);
        return rebaseCommand.call();
    }

    /**
     * checkout branch.
     * @param branchName branch name
     * @throws RefAlreadyExistsException
     * @throws RefNotFoundException
     * @throws InvalidRefNameException
     */
    protected void checkoutTo(String branchName) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException {
        CheckoutCommand checkoutCommand = getGit().checkout();
        checkoutCommand.setName(branchName);
        checkoutCommand.call();
    }

    /**
     * call add -A
     * @throws NoFilepatternException exception when git add called
     */
    protected void addAll() throws NoFilepatternException {
        AddCommand addCommand = getGit().add();
        addCommand.addFilepattern(".");
        addCommand.call();
    }

    /**
     * reset to indicated revision.
     * @param commit commit
     * @param resetType reset type
     * @throws IOException IO exception when command is called.
     */
    protected Ref resetTo(RevCommit commit, ResetType resetType) throws IOException {
        ResetCommand resetCommand = getGit().reset();
        resetCommand.setMode(resetType);
        resetCommand.setRef(commit.getId().getName());
        return resetCommand.call();
    }

    /**
     * get commit message (for mock).
     * @param commit commit
     * @return message
     */
    protected String getShortMessage(RevCommit commit) {
        return commit.getShortMessage();
    }

    /**
     * get now messages.
     * @return now message
     */
    protected String getNowMessage() {
        String prefix = "[from now]";
        DateFormat format = new SimpleDateFormat(" yyyy/MM/dd hh:mm:ss");
        String date = format.format(getDate());
        return prefix + date;
    }

    /**
     * get time (for mock).
     * @return now
     */
    protected Date getDate() {
        return new Date();
    }

    /**
     * get JGit API.
     * @return Git
     */
    protected Git getGit() {
        return new Git(this.repository);
    }
}
