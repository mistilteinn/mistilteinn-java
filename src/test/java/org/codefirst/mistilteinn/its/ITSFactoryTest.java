package org.codefirst.mistilteinn.its;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.codefirst.mistilteinn.config.MistilteinnConfiguration;
import org.codefirst.mistilteinn.its.redmine.RedmineAdapter;
import org.junit.Test;

public class ITSFactoryTest {

    @Test
    public void testCreateITSNull() throws Exception {
        MistilteinnConfiguration mockedConfiguration = mock(MistilteinnConfiguration.class);
        ITS its = ITSFactory.createITS(mockedConfiguration);
        assertThat(its, is(nullValue()));
    }

    @Test
    public void testCreateITS() throws Exception {
        MistilteinnConfiguration mockedConfiguration = mock(MistilteinnConfiguration.class);
        doReturn("redmine").when(mockedConfiguration).getITS();
        ITS its = ITSFactory.createITS(mockedConfiguration);
        assertThat(its.getClass().getName(), is(RedmineAdapter.class.getName()));
    }

}
