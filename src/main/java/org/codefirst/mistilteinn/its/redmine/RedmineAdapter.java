package org.codefirst.mistilteinn.its.redmine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codefirst.mistilteinn.MistilteinnException;
import org.codefirst.mistilteinn.its.ITS;
import org.codefirst.mistilteinn.its.Ticket;
import org.redmine.ta.AuthenticationException;
import org.redmine.ta.NotFoundException;
import org.redmine.ta.RedmineException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Issue;

/**
 * Redmine ITS Adapter.
 */
public class RedmineAdapter implements ITS {
    /** redmine id. */
    public static final String ID = "redmine";

    /** the key of options (URL of Redmine). */
    private static final String OPTION_URL = "url";
    /** the key of options (API Key of Redmine). */
    private static final String OPTION_APIKEY = "apikey";
    /** the key of options (Project id of Redmine). */
    private static final String OPTION_PROJECT = "project";

    /** options */
    private Map<String, String> options;

    /** the URL of Redmine. */
    private String redmineUrl;
    /** the API key of Redmine. */
    private String redmineApiKey;
    /** the project id of Redmine. */
    private String projectId;

    /**
     * Constructor.
     * @param configuration configuration map
     */
    public RedmineAdapter(Map<String, String> configuration) {
        this.options = configuration;
        this.redmineUrl = options.get(OPTION_URL);
        this.redmineApiKey = options.get(OPTION_APIKEY);
        this.projectId = options.get(OPTION_PROJECT);
    }

    /** {@inheritDoc} */
    public Ticket[] listTickets() throws MistilteinnException {
        RedmineManager mgr = getRedmineManager();
        List<Ticket> list = new ArrayList<Ticket>();
        try {
            List<Issue> issues = mgr.getIssues(projectId, null);
            for (Issue issue : issues) {
                list.add(new Ticket(issue.getId(), issue.getSubject()));
            }
        } catch (IOException e) {
            throw new MistilteinnException(e);
        } catch (AuthenticationException e) {
            throw new MistilteinnException(e);
        } catch (NotFoundException e) {
            throw new MistilteinnException(e);
        } catch (RedmineException e) {
            throw new MistilteinnException(e);
        }
        return (Ticket[]) list.toArray(new Ticket[list.size()]);
    }

    /**
     * Get an instance of RedmineManager.
     * @return an instance of RedmineManager
     */
    protected RedmineManager getRedmineManager() {
        return new RedmineManager(redmineUrl, redmineApiKey);
    }

}
