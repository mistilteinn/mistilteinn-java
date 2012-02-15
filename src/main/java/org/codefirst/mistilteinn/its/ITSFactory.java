package org.codefirst.mistilteinn.its;

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
     * @param itsId identifier of ITS
     * @return an instance of ITS
     */
    ITS createITS(String itsId) {
        // TODO should be pluggable and configurable
        return new RedmineAdapter();
    }

}
