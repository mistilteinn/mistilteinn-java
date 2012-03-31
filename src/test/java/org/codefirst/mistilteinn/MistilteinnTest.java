package org.codefirst.mistilteinn;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import org.codefirst.mistilteinn.config.MistilteinnConfiguration;
import org.codefirst.mistilteinn.its.ITS;
import org.codefirst.mistilteinn.its.Ticket;
import org.codefirst.mistilteinn.vcs.GitAdapter;
import org.junit.Test;

public class MistilteinnTest {

    @Test
    public void testInit() throws Exception {
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        File configFile = new File("/path/to/.mistilteinn/config.yaml");
        doReturn(configFile).when(mistilteinn).createConfigFile();
        MistilteinnConfiguration mockedConfig = mock(MistilteinnConfiguration.class);
        doReturn(mockedConfig).when(mistilteinn).getMistilteinnConfiguration();
        OutputStream mockedOutputStream = mock(OutputStream.class);
        doReturn(mockedOutputStream).when(mistilteinn).getConfigFileOutputStream(configFile);

        mistilteinn.init();

        verify(mockedConfig).save(mockedOutputStream);
    }

    @Test
    public void testGetInitialRedmineConfigration() throws Exception {
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        Map<String, String> configration = mistilteinn.getInitialRedmineConfigration();
        assertThat(configration.containsKey("url"), is(true));
        assertThat(configration.containsKey("project"), is(true));
        assertThat(configration.containsKey("apikey"), is(true));
    }

    @Test
    public void testGetInitialGithubConfigration() throws Exception {
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        Map<String, String> configration = mistilteinn.getInitialGithubConfigration();
        assertThat(configration.containsKey("name"), is(true));
        assertThat(configration.containsKey("project"), is(true));
    }

    @Test
    public void testTicket() throws Exception {
        int ticketId = 100;
        GitAdapter mockedGitAdapter = mock(GitAdapter.class);
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        doReturn(mockedGitAdapter).when(mistilteinn).getVCSAdapter();
        ITS mockedITS = mock(ITS.class);
        doReturn(new Ticket(Integer.valueOf(ticketId), "subject")).when(mockedITS).getTicket(ticketId);
        doReturn(mockedITS).when(mistilteinn).getITS();

        mistilteinn.ticket(100);

        verify(mockedGitAdapter).ticket(100);
    }

    @Test
    public void testMasterize() throws Exception {
        GitAdapter mockedGitAdapter = mock(GitAdapter.class);
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        doReturn(mockedGitAdapter).when(mistilteinn).getVCSAdapter();

        mistilteinn.masterize();

        verify(mockedGitAdapter).masterize("master");
    }

    @Test
    public void testMasterizeWithBranchName() throws Exception {
        String branchName = "branch";
        GitAdapter mockedGitAdapter = mock(GitAdapter.class);
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        doReturn(mockedGitAdapter).when(mistilteinn).getVCSAdapter();

        mistilteinn.masterize(branchName);

        verify(mockedGitAdapter).masterize(branchName);
    }

    @Test
    public void testGetCurrentBranch() throws Exception {
        String branch = "id/100";
        GitAdapter mockedGitAdapter = mock(GitAdapter.class);
        Mistilteinn mistilteinn = spy(new Mistilteinn(""));
        doReturn(mockedGitAdapter).when(mistilteinn).getVCSAdapter();
        doReturn(branch).when(mockedGitAdapter).getCurrentBranchName();
        assertThat(mistilteinn.getCurrentBranchName(), is(branch));
    }
}
