package com.oberasoftware.home.core.mqtt;

import io.moquette.broker.security.IAuthorizatorPolicy;
import io.moquette.broker.subscriptions.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Renze de Vries
 */
public class TestAuthorizer implements IAuthorizatorPolicy {
    private static final Logger LOG = LoggerFactory.getLogger(TestAuthorizer.class);

    @Override
    public boolean canWrite(Topic topic, String s, String s1) {
        LOG.info("Can write: {}, {}, {}", topic, s, s1);
        return topic.toString().startsWith("/states") && s1.equals("piet");
    }

    @Override
    public boolean canRead(Topic topic, String s, String s1) {
        return true;
    }
}
