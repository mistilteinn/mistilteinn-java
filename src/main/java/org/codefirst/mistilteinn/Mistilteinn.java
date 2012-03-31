package org.codefirst.mistilteinn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

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
    /** project root path */
    private String projectPath;

    /**
     * constructor.
     * @param projectPath path to project root
     * @throws IOException
     */
    public Mistilteinn(String projectPath) throws IOException {
        this.projectPath = projectPath;
    }

    /**
     * initialize configuration.
     * @throws MistilteinnException cannot create configuration
     */
    public void init() throws MistilteinnException {
        File configFile = createConfigFile();
        OutputStream os = null;
        try {
            MistilteinnConfiguration config = getMistilteinnConfiguration();
            os = getConfigFileOutputStream(configFile);
            config.save(os);
        } catch (FileNotFoundException e) {
            throw new MistilteinnException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new MistilteinnException(e);
                }
            }
        }
    }

    protected OutputStream getConfigFileOutputStream(File configFile) throws FileNotFoundException {
        return new FileOutputStream(configFile);
    }

    protected MistilteinnConfiguration getMistilteinnConfiguration() throws MistilteinnException {
        MistilteinnConfiguration config = MistilteinnConfigurationFactory.createConfiguration(this.projectPath);
        config.setITS("redmine");
        config.setConfiguration("redmine", getInitialRedmineConfigration());
        config.setConfiguration("github", getInitialGithubConfigration());
        return config;
    }

    protected File createConfigFile() throws MistilteinnException {
        // Fixme duprecated path string from YamlMistilteinnConfiguration class
        File configDir = new File(this.projectPath, ".mistilteinn");
        if (!configDir.exists()) {
            if (!configDir.mkdir()) {
                throw new MistilteinnException(new Exception("cannot create " + configDir.getAbsolutePath()));
            }
        }
        File configFile = new File(configDir, "config.yaml");
        try {
            if (!configFile.exists()) {
                if (!configFile.createNewFile()) {
                    throw new MistilteinnException(new Exception("cannot create " + configFile.getAbsolutePath()));
                }
            }
        } catch (IOException e) {
            throw new MistilteinnException(e);
        }
        return configFile;
    }

    protected Map<String, String> getInitialGithubConfigration() {
        Map<String, String> config = new LinkedHashMap<String, String>();
        config.put("name", "your_id");
        config.put("project", "your_project_name");
        return config;
    }

    protected Map<String, String> getInitialRedmineConfigration() {
        Map<String, String> config = new LinkedHashMap<String, String>();
        config.put("url", "https://example.com/redmine/");
        config.put("project", "some-project-name");
        config.put("apikey", "your_key");
        return config;
    }

    /**
     * change branch to id/#{ticketId}.
     * @param ticketId ticket id
     * @return specified ticket
     * @throws MistilteinnException exceptions
     */
    public Ticket ticket(int ticketId) throws MistilteinnException {
        getVCSAdapter().ticket(ticketId);
        Ticket ticket = getITS().getTicket(ticketId);
        System.out.println(ticket.toString());
        return ticket;
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
        this.masterize("master");
    }

    /**
     * rebase to a branch.
     * @param branchName branch name
     */
    public void masterize(String branchName) throws MistilteinnException {
        getVCSAdapter().masterize(branchName);
    }

    /**
     * list tickets.
     * @return tickets
     * @throws MistilteinnException fail to access to ITS
     */
    public Ticket[] list() throws MistilteinnException {
        Ticket[] tickets = getITS().listTickets();
        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }
        return tickets;
    }

    public String getCurrentBranchName() throws MistilteinnException {
        return getVCSAdapter().getCurrentBranchName();
    }

    /**
     * get a VCS adapter object.
     * @return vcs adapter object
     * @throws IOException cannot access git directory
     */
    protected GitAdapter getVCSAdapter() throws MistilteinnException {
        if (this.gitAdapter == null) {
            try {
                gitAdapter = new GitAdapter(new File(projectPath, ".git").getAbsolutePath());
            } catch (IOException e) {
                throw new MistilteinnException(e);
            }
        }
        return this.gitAdapter;
    }

    /**
     * get ITS object.
     * @return ITS
     * @throws MistilteinnException fail to get its object
     */
    protected ITS getITS() throws MistilteinnException {
        File scmDirectory = getVCSAdapter().getDirectory();
        File projectDirectory = scmDirectory.getParentFile();
        String projectPath = ".";
        if (projectDirectory != null) {
            projectPath = projectDirectory.getPath();
        }
        MistilteinnConfiguration config = MistilteinnConfigurationFactory.createConfiguration(projectPath);
        return ITSFactory.createITS(config);
    }

    public static void main(String[] args) throws Exception {
        Mistilteinn mistilteinn = new Mistilteinn(args[0]);
        if (StringUtils.equals(args[1], "init")) {
            mistilteinn.init();
        } else if (StringUtils.equals(args[1], "ticket")) {
            mistilteinn.ticket(Integer.valueOf(args[2]));
        } else if (StringUtils.equals(args[1], "now")) {
            mistilteinn.now();
        } else if (StringUtils.equals(args[1], "fixup")) {
            mistilteinn.fixup(args[2]);
        } else if (StringUtils.equals(args[1], "masterize")) {
            mistilteinn.masterize();
        } else if (StringUtils.equals(args[1], "list")) {
            mistilteinn.list();
        } else if (StringUtils.equals(args[1], "currentBranch")) {
            System.out.println(mistilteinn.getCurrentBranchName());
        }
    }
}
