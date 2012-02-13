package org.codefirst.mistilteinn.vcs;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
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
     * @throws InvalidRefNameException
     * @throws RefNotFoundException
     * @throws RefAlreadyExistsException
     * @throws JGitInternalException
     */
    public boolean ticket(int ticketId) throws JGitInternalException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException {
        CheckoutCommand checkoutCommand = getGit().checkout();
        checkoutCommand.setForce(true);
        checkoutCommand.setName("id/" + ticketId);
        checkoutCommand.setCreateBranch(true);
        checkoutCommand.call();
        return true;
    }

    /**
     * get JGit API.
     * @return Git
     */
    protected Git getGit() {
        return new Git(this.repository);
    }

}