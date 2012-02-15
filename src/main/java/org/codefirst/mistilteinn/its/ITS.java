package org.codefirst.mistilteinn.its;

import java.util.Map;

import org.codefirst.mistilteinn.MistilteinnException;

/**
 * Issue Tracking System interface.
 */
public interface ITS {

    /**
     * Set options map.
     * @param options the options map
     */
    void setOptions(Map<String, String> options);

    /**
     * Get options map.
     * @return the options map
     */
    Map<String, String> getOptions();

    /**
     * List tickets of specified project.
     * @param projectId the project identifier
     * @return tickets
     */
    Ticket[] listTickets(String projectId) throws MistilteinnException;

}
