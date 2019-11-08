package com.oberasoftware.robo.maximus.storage;

import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.exceptions.JasDBStorageException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.rest.client.RestDBSession;
import com.oberasoftware.jasdb.service.local.LocalDBSession;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import static com.oberasoftware.jasdb.core.utils.StringUtils.stringNotEmpty;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
//@Component
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
        if(stringNotEmpty(jasdbMode) && jasdbMode.equals("rest")) {
            LOG.debug("Creating JasDB REST session to host: {} port: {} instance: {}", jasdbHost, jasdbPort, jasdbInstance);
            session = new RestDBSession(jasdbInstance, jasdbHost, jasdbPort);
        } else {
            LOG.debug("Creating JasDB Local session to instance: {}", jasdbInstance);
            session = new LocalDBSession(jasdbInstance);
        }
        handleWipeData(session);

        return session;
    }

    private void handleWipeData(DBSession session) throws JasDBStorageException {
        if(wipeStartup) {
//            LOG.debug("Deleting data on startup was enabled");
//            session.getBags().forEach(b -> {
//                try {
//                    String bagName = b.getName();
//                    LOG.debug("Removing data from bag: {}", bagName);
//                    session.removeBag(bagName);
//                } catch (JasDBStorageException e) {
//                    LOG.error("Unable to remove data from bag: " + b, e);
//                }
//            });
        }
    }
}
