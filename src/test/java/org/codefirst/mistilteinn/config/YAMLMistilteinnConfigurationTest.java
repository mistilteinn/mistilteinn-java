package org.codefirst.mistilteinn.config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class YAMLMistilteinnConfigurationTest {

    private YAMLMistilteinnConfiguration config;

    @Before
    public void Setup() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("config.yaml");
        this.config = new YAMLMistilteinnConfiguration(is);
        is.close();
    }

    @Test
    public void testGetITS() {
        String result = this.config.getITS();
        assertThat(result, is("redmine"));
    }

    @Test
    public void testSetITS() {
        this.config.setITS("trac");
        String result = this.config.getITS();
        assertThat(result, is("trac"));
    }

    @Test
    public void testGetConfiguration() throws Exception {
        Map<String, String> configuration = this.config.getConfiguration("redmine");
        String result = configuration.get("url");
        assertThat(result, is("https://example.com/redmine/"));
    }

    @Test
    public void testSetConfiguration() throws Exception {
        Map<String, String> configuration = new LinkedHashMap<String, String>();
        configuration.put("bar", "baz");
        this.config.setConfiguration("foo", configuration);

        Map<String, String> newConfiguration = this.config.getConfiguration("foo");
        String result = newConfiguration.get("bar");
        assertThat(result, is("baz"));
    }

    @Test
    public void testSaveShouldSave() throws Exception {
        OutputStream os = mock(OutputStream.class);
        this.config.save(os);
        verify(os).close();
    }

}
