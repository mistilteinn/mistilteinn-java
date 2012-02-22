package org.codefirst.mistilteinn.config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codefirst.mistilteinn.MistilteinnException;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

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

    @Test(expected = MistilteinnException.class)
    public void testSaveThrowsIOException() throws Exception {
        config = spy(config);
        OutputStream os = mock(OutputStream.class);
        OutputStreamWriter osw = mock(OutputStreamWriter.class);
        doReturn(osw).when(config).getOutputStreamWriter(os);
        doThrow(mock(IOException.class)).when(osw).write(anyString());
        this.config.save(os);

    }

    @Test
    public void testYamlDump() throws Exception {
        LinkedHashMap<String, Object> ticket = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, String> source = new LinkedHashMap<String, String>();
        source.put("source", "redmine");
        ticket.put("ticket", source);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        assertThat(yaml.dump(ticket), is("ticket:\n  source: redmine\n"));
    }
}
