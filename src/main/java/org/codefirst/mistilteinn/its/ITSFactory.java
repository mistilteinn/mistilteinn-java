package org.codefirst.mistilteinn.its;

import org.apache.commons.lang.StringUtils;
import org.codefirst.mistilteinn.config.MistilteinnConfiguration;
import org.codefirst.mistilteinn.its.redmine.RedmineAdapter;

/**
 * A Factory for ITS.
 */
public class ITSFactory {

    /**
     * invisible constructor.
     */
    private ITSFactory() {
        super();
    }

    /**
     * create an instance of ITS.
     * @param config configuration
     * @return an instance of ITS
     */
    public static ITS createITS(MistilteinnConfiguration config) {
        if (StringUtils.equals(config.getITS(), RedmineAdapter.ID)) {
            return new RedmineAdapter(config.getConfiguration(RedmineAdapter.ID));
        }
        return null;
    }

}
