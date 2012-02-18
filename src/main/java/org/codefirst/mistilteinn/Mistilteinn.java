package org.codefirst.mistilteinn;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.codefirst.mistilteinn.config.MistilteinnConfiguration;
import org.codefirst.mistilteinn.config.MistilteinnConfigurationFactory;
import org.codefirst.mistilteinn.its.ITS;
import org.codefirst.mistilteinn.its.ITSFactory;
import org.codefirst.mistilteinn.its.Ticket;
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
        this(new GitAdapter(gitPath));
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

    /**
     * commit temporary
     * @throws MistilteinnException exception
     */
    public void now() throws MistilteinnException {
        getVCSAdapter().now();
    }

    /**
     * fixup now commmits.
     * @param message commit message
     * @throws MistilteinnException exception
     */
    public void fixup(String message) throws MistilteinnException {
        getVCSAdapter().fixup(message);
    }

    /**
     * rebase to master branch.
     */
    public void masterize() throws MistilteinnException {
        getVCSAdapter().masterize();
    }

    /**
     * list tickets.
     * @throws MistilteinnException fail to access to ITS
     */
    public void list() throws MistilteinnException {
        MistilteinnConfiguration config = MistilteinnConfigurationFactory.createConfiguration(".");
        ITS its = ITSFactory.createITS(config);
        Ticket[] tickets = its.listTickets();
        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }
    }

    /**
     * get a VCS adapter object.
     * @return vcs adapter object
     */
    protected GitAdapter getVCSAdapter() {
        return this.gitAdapter;
    }

    public static void main(String[] args) throws Exception {
        Mistilteinn mistilteinn = new Mistilteinn(args[0]);
        if (StringUtils.equals(args[1], "ticket")) {
            mistilteinn.ticket(Integer.valueOf(args[2]));
        } else if (StringUtils.equals(args[1], "now")) {
            mistilteinn.now();
        } else if (StringUtils.equals(args[1], "fixup")) {
            mistilteinn.fixup(args[2]);
        } else if (StringUtils.equals(args[1], "masterize")) {
            mistilteinn.masterize();
        } else if (StringUtils.equals(args[1], "list")) {
            mistilteinn.list();
        }
    }
}
