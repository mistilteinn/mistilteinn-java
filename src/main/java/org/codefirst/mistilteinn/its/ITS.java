package org.codefirst.mistilteinn.its;

import org.codefirst.mistilteinn.MistilteinnException;

/**
 * Issue Tracking System interface.
 */
public interface ITS {

    /**
     * List tickets of specified project.
     * @param projectId the project identifier
     * @return tickets
     */
    Ticket[] listTickets() throws MistilteinnException;

}
