package org.codefirst.mistilteinn;

import java.io.IOException;

import org.codefirst.mistilteinn.vcs.GitAdapter;

/**
 * Mistilteinn Main API
 */
public class Mistilteinn {
    /** target repository. */
    private GitAdapter gitAdapter;

    /**
     * constructor.
     * @param gitPath path to repository (.git)
     * @throws IOException
     */
    public Mistilteinn(String gitPath) throws IOException {
        this.gitAdapter = new GitAdapter(gitPath);
    }

    /**
     * constructor.
     * @param adapter vcs adapter
     */
    public Mistilteinn(GitAdapter adapter) {
        this.gitAdapter = adapter;
    }

    /**
     * change branch to id/#{ticketId}.
     * @param ticketId ticket id
     * @return true if success
     * @throws MistilteinnException exceptions
     */
    public void ticket(int ticketId) throws MistilteinnException {
        getVCSAdapter().ticket(ticketId);
    }

    public GitAdapter getVCSAdapter() {
        return this.gitAdapter;
    }
}
