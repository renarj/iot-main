package com.oberasoftware.home.hue;

import com.oberasoftware.home.api.model.Device;
import com.oberasoftware.home.api.model.Status;
import io.github.zeroone3010.yahueapi.Hue;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class HueDeviceManager {
    private static final Logger LOG = getLogger(HueDeviceManager.class);

    @Autowired
    private HueConnector hueConnector;

    public List<Device> getDevices() {

        List<Hue> bridges = hueConnector.getBridges();

        List<Device> devicesFound = new ArrayList<>();
        bridges.forEach(b -> {
            LOG.info("Getting lights for bridge: {}", b);
            devicesFound.addAll(b.getAllLights().getLights().stream().map(l -> {
                String lightId = l.getId();
                String name = l.getName();

                Map<String, String> properties = new HashMap<>();
                properties.put("lightType", l.getType().name());

                LOG.debug("Found a Hue Light id: {} with name: {}", lightId, name);
                return new HueDevice(lightId, name, Status.DISCOVERED, properties);
            }).toList());
        });
        return devicesFound;
//
//        PHBridge bridge = sdk.getSelectedBridge();
//        if(bridge != null) {
//            return bridge.getResourceCache().getAllLights().stream()
//                    .map(l -> {
//                        String lightId = l.getIdentifier();
//                        String name = l.getName();
//
//                        Map<String, String> properties = new HashMap<>();
//                        properties.put("modelNumber", l.getModelNumber());
//                        properties.put("version", l.getVersionNumber());
//                        properties.put("lightType", l.getLightType().name());
//
//                        LOG.debug("Found a Hue Light id: {} with name: {}", l.getIdentifier(), l.getName());
//
//                        return new HueDevice(lightId, name, Status.DISCOVERED, properties);
//                    }).collect(Collectors.toList());
//        } else {
//            LOG.debug("No Hue bridge was connected");
//        }
//
//        return new ArrayList<>();
    }
}
