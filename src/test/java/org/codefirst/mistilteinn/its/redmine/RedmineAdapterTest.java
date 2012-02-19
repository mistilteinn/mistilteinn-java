package org.codefirst.mistilteinn.its.redmine;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codefirst.mistilteinn.MistilteinnException;
import org.codefirst.mistilteinn.its.Ticket;
import org.junit.Before;
import org.junit.Test;
import org.redmine.ta.AuthenticationException;
import org.redmine.ta.NotFoundException;
import org.redmine.ta.RedmineException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.RedmineManager.INCLUDE;
import org.redmine.ta.beans.Issue;

public class RedmineAdapterTest {

    private Map<String, String> configuration = new LinkedHashMap<String, String>();

    @Before
    public void setUp() {
        this.configuration.put("url", "http://redmine.org");
        this.configuration.put("project", "projectId");
        this.configuration.put("apikey", "APIKEY");
    }

    @Test
    public void testListTickets() throws Exception {
        Issue mockedIssue = mock(Issue.class);
        doReturn("subject").when(mockedIssue).getSubject();
        doReturn(Integer.valueOf(1)).when(mockedIssue).getId();

        List<Issue> issues = new ArrayList<Issue>();
        issues.add(mockedIssue);

        RedmineManager mockedRedmineManager = mock(RedmineManager.class);
        doReturn(issues).when(mockedRedmineManager).getIssues("projectId", null);

        RedmineAdapter redmineAdapter = spy(new RedmineAdapter(this.configuration));
        doReturn(mockedRedmineManager).when(redmineAdapter).getRedmineManager();

        Ticket[] tickets = redmineAdapter.listTickets();
        assertThat(tickets.length, is(1));
        assertThat(tickets[0].getId(), is(Integer.valueOf(1)));
        assertThat(tickets[0].getSubject(), is("subject"));
    }

    @Test(expected = MistilteinnException.class)
    public void testListTicketsWithIOException() throws Exception {
        RedmineManager mockedRedmineManager = mock(RedmineManager.class);
        doThrow(new IOException()).when(mockedRedmineManager).getIssues("projectId", null);

        RedmineAdapter redmineAdapter = spy(new RedmineAdapter(this.configuration));
        doReturn(mockedRedmineManager).when(redmineAdapter).getRedmineManager();

        redmineAdapter.listTickets();
    }

    @Test
    public void testGetTicket() throws Exception {
        Issue mockedIssue = mock(Issue.class);
        doReturn("subject").when(mockedIssue).getSubject();
        doReturn(Integer.valueOf(1)).when(mockedIssue).getId();

        RedmineManager mockedRedmineManager = mock(RedmineManager.class);
        doReturn(mockedIssue).when(mockedRedmineManager).getIssueById(Integer.valueOf(1), INCLUDE.journals);

        RedmineAdapter redmineAdapter = spy(new RedmineAdapter(this.configuration));
        doReturn(mockedRedmineManager).when(redmineAdapter).getRedmineManager();

        Ticket ticket = redmineAdapter.getTicket(1);

        assertThat(ticket.getId(), is(Integer.valueOf(1)));
        assertThat(ticket.getSubject(), is("subject"));
    }

    @Test(expected = MistilteinnException.class)
    public void testGetTicketWithIOException() throws Exception {
        Exception exception = new IOException();
        getTicketWithException(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testGetTicketWithAuthenticationException() throws Exception {
        Exception exception = mock(AuthenticationException.class);
        getTicketWithException(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testGetTicketWithNotFoundException() throws Exception {
        Exception exception = mock(NotFoundException.class);
        getTicketWithException(exception);
    }

    @Test(expected = MistilteinnException.class)
    public void testGetTicketWithRedmineException() throws Exception {
        Exception exception = mock(RedmineException.class);
        getTicketWithException(exception);
    }

    private void getTicketWithException(Exception exception) throws IOException, AuthenticationException, NotFoundException, RedmineException, MistilteinnException {
        RedmineManager mockedRedmineManager = mock(RedmineManager.class);
        doThrow(exception).when(mockedRedmineManager).getIssueById(Integer.valueOf(1), INCLUDE.journals);

        RedmineAdapter redmineAdapter = spy(new RedmineAdapter(this.configuration));
        doReturn(mockedRedmineManager).when(redmineAdapter).getRedmineManager();

        redmineAdapter.getTicket(1);
    }

}
