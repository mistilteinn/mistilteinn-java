package org.codefirst.mistilteinn.vcs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codefirst.mistilteinn.MistilteinnException;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
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
     * get JGit API.
     * @return Git
     */
    protected Git getGit() {
        return new Git(this.repository);
    }

}
