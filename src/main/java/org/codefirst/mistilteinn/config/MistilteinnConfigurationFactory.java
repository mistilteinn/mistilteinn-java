package org.codefirst.mistilteinn.config;

import java.io.FileNotFoundException;

import org.codefirst.mistilteinn.MistilteinnException;

/**
 * A factory class for configuration.
 */
public class MistilteinnConfigurationFactory {

    /**
     * Invisible constructor.
     */
    private MistilteinnConfigurationFactory() {
        super();
    }

    /**
     * create configuration accessor.
     * @param projectPath project path
     * @return configuration accessor
     * @throws MistilteinnException io error
     */
    public static MistilteinnConfiguration createConfiguration(String projectPath) throws MistilteinnException {
        try {
            return new YAMLMistilteinnConfiguration(projectPath);
        } catch (FileNotFoundException e) {
            throw new MistilteinnException(e);
        }
    }

}
