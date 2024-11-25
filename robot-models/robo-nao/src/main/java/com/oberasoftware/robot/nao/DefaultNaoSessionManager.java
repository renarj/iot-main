package com.oberasoftware.robot.nao;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.Session;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author Renze de Vries
 */
@Component
public class DefaultNaoSessionManager implements NaoSessionManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultNaoSessionManager.class);

    @Value("${nao.url}")
    private String robotUrl;

    private Application application;

    @PostConstruct
    public void initialize() {
        LOG.info("Connecting to robot on url: {}", robotUrl);
        application = new Application(new String[] {}, robotUrl);
        application.start();
    }

    @Override
    public Session getSession() {
        return application.session();
    }
}
