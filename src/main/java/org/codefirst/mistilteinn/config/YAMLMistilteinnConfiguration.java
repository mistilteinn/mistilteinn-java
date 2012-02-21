package org.codefirst.mistilteinn.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.codefirst.mistilteinn.MistilteinnException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Configuration accessor with YAML.
 */
@SuppressWarnings("unchecked")
public class YAMLMistilteinnConfiguration implements MistilteinnConfiguration {
    /** the path to yaml file. */
    private static final String MISTILTEINN_CONFIG_YAML = ".mistilteinn/config.yaml";
    /** the key of its configuration. */
    private static final String TICKET = "ticket";
    /** the sub key of its configuration. */
    private static final String SOURCE = "source";

    /** configuration representation. */
    private Map<String, Object> yaml;

    /**
     * Constructor.
     * @param projectPath the path of project
     * @throws FileNotFoundException io error
     */
    public YAMLMistilteinnConfiguration(String projectPath) throws FileNotFoundException {
        this(new FileInputStream(new File(projectPath, MISTILTEINN_CONFIG_YAML)));
    }

    /**
     * Constructor.
     * @param is input stream
     */
    public YAMLMistilteinnConfiguration(InputStream is) {
        Yaml parser = new Yaml();
        yaml = (Map<String, Object>) parser.load(is);
        if (yaml == null) {
            yaml = new LinkedHashMap<String, Object>();
        }
    }

    public String getITS() {
        Map<String, Object> ticket = (Map<String, Object>) yaml.get(TICKET);
        return ObjectUtils.toString(ticket.get(SOURCE));
    }

    public void setITS(String id) {
        Map<String, Object> ticket = (Map<String, Object>) yaml.get(TICKET);
        if (ticket == null) {
            ticket = new LinkedHashMap<String, Object>();
            yaml.put(TICKET, ticket);
        }
        ticket.put(SOURCE, id);
    }

    public Map<String, String> getConfiguration(String id) {
        Map<String, String> map = new LinkedHashMap<String, String>();

        Map<String, Object> section = (Map<String, Object>) yaml.get(id);

        Set<Entry<String, Object>> entrySet = section.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            map.put(entry.getKey(), ObjectUtils.toString(entry.getValue()));
        }

        return map;
    }

    public void setConfiguration(String id, Map<String, String> configuration) {
        yaml.put(id, configuration);
    }

    public void save(OutputStream os) throws MistilteinnException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String dump = new Yaml(options).dump(yaml);
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(dump);
        } catch (IOException e) {
            throw new MistilteinnException(e);
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException e) {
                // do nothing
            }
        }
    }

}
