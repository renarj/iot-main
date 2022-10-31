package com.oberasoftware.home.hue;

import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.ControllerConfiguration;
import com.oberasoftware.iot.core.events.ThingValueEvent;
import com.oberasoftware.iot.core.events.impl.ThingValueEventImpl;
import com.oberasoftware.iot.core.legacymodel.OnOffValue;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class HueLightMonitor {
    private static final Logger LOG = getLogger(HueLightMonitor.class);

    private static final int MONITOR_INTERVAL = 60000;

    private final HueConnector hueConnector;

    private final HueDeviceManager deviceManager;

    private final LocalEventBus bus;

    private final ControllerConfiguration controllerConfiguration;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public HueLightMonitor(HueConnector hueConnector, HueDeviceManager deviceManager, LocalEventBus bus, ControllerConfiguration controllerConfiguration) {
        this.hueConnector = hueConnector;
        this.deviceManager = deviceManager;
        this.bus = bus;
        this.controllerConfiguration = controllerConfiguration;
    }

    @PostConstruct
    public void start() {
        LOG.debug("Scheduling regular light state checks");
        scheduledExecutorService.scheduleAtFixedRate(this::checkAllLightStates, 0, MONITOR_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void stop() {
        scheduledExecutorService.shutdown();
    }

    public void checkAllLightStates() {
        if(hueConnector.isConnected()) {
            hueConnector.getBridges().forEach(b -> {
                LOG.info("Checking hue light state for bridge: {}", b.getBridgeId());
                List<HueDeviceState> states = deviceManager.retrieveStates(b.getBridgeId());
                states.forEach(this::checkLightState);
            });

        } else {
            LOG.debug("Skipping light state check, not connected to bridge");
        }
    }

    public void checkLightState(HueDeviceState light) {
        LOG.debug("Checking light state: {}", light);
        String deviceId = light.getLightId();

        bus.publish(new ThingValueEventImpl(controllerConfiguration.getControllerId(),
                deviceId, light.getOnOffState(), OnOffValue.LABEL));

        int brightness = light.getBrightness();
        int correctedScale = (int)((double)brightness/255 * 100);
        Value value = new ValueImpl(VALUE_TYPE.NUMBER, correctedScale);
        ThingValueEvent valueEvent = new ThingValueEventImpl(controllerConfiguration.getControllerId(),
                deviceId, value, "value");
        bus.publish(valueEvent);
    }
}
