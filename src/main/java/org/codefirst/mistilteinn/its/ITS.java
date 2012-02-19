package org.codefirst.mistilteinn.its;

import org.codefirst.mistilteinn.MistilteinnException;

/**
 * Issue Tracking System interface.
 */
public interface ITS {

    /**
     * List tickets of specified project.
     * @return tickets
     */
    Ticket[] listTickets() throws MistilteinnException;

    /**
     * get a ticket of specified project. <br/>
     * if not found, returns null
     * @param ticketId ticket id
     * @return a ticket
     */
    Ticket getTicket(int ticketId) throws MistilteinnException;
}
