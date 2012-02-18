package org.codefirst.mistilteinn.config;

import java.io.OutputStream;
import java.util.Map;

import org.codefirst.mistilteinn.MistilteinnException;

/**
 * Configuration accessor.
 */
public interface MistilteinnConfiguration {

    /**
     * Get ITS identifier.
     * @return ITS identifier
     */
    String getITS();

    /**
     * Set ITS identifier.
     * @param id ITS identifier
     */
    void setITS(String id);

    /**
     * Get configuration of sub section.
     * @param id section id
     * @return configuration mapping
     */
    Map<String, String> getConfiguration(String id);

    /**
     * Set configuration of sub section.
     * @param id
     * @param configuration
     */
    void setConfiguration(String id, Map<String, String> configuration);

    /**
     * save current configuration.
     * @param os the output stream
     * @throws MistilteinnException io error
     */
    void save(OutputStream os) throws MistilteinnException;

}
