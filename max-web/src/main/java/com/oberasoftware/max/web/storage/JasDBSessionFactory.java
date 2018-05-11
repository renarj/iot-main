package com.oberasoftware.max.web.storage;

import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.service.local.LocalDBSession;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class JasDBSessionFactory {
    private static final Logger LOG = getLogger(JasDBSessionFactory.class);

    @Value("${jasdb.mode:local}")
    private String jasdbMode;

    @Value("${jasdb.wipe.startup:false}")
    private boolean wipeStartup;

    @Value("${jasdb.host:}")
    private String jasdbHost;

    @Value("${jasdb.post:7050}")
    private int jasdbPort;

    @Value("${jasdb.instance:default}")
    private String jasdbInstance;


    public DBSession createSession() throws JasDBException {
        DBSession session;
//        if(stringNotEmpty(jasdbMode) && jasdbMode.equals("rest")) {
//            LOG.debug("Creating JasDB REST session to host: {} port: {} instance: {}", jasdbHost, jasdbPort, jasdbInstance);
//            session = new RestDBSession(jasdbInstance, jasdbHost, jasdbPort);
//        } else {
            LOG.debug("Creating JasDB Local session to instance: {}", jasdbInstance);
            session = new LocalDBSession(jasdbInstance);
//        }

        return session;
    }
}
