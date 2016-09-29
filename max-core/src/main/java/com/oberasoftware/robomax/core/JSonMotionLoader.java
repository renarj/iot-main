package com.oberasoftware.robomax.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.api.motion.MotionConverter;
import com.oberasoftware.robo.core.motion.MotionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author Renze de Vries
 */
@Component
public class JSonMotionLoader implements MotionConverter {
    private static final Logger LOG = LoggerFactory.getLogger(JSonMotionLoader.class);

    @Override
    public List<Motion> loadMotions(String motionFile) {
        try {
            URL resource = this.getClass().getResource(motionFile);
            if(resource != null) {
                LOG.debug("Opening resource: {}", resource.toURI());
                Path path = Paths.get(resource.toURI());

                String json = new String(Files.readAllBytes(path), "utf-8");
                LOG.debug("Loaded json resource: {}", json);

                return Lists.newArrayList(new ObjectMapper().readValue(json, MotionImpl.class));
            } else {
                LOG.error("Could not find motion file: {}", motionFile);
            }
        } catch (URISyntaxException | IOException e) {
            LOG.error("Could not read file", e);
        }

        return emptyList();
    }
}
