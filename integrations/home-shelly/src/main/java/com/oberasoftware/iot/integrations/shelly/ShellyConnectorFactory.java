package com.oberasoftware.iot.integrations.shelly;

import com.oberasoftware.iot.core.exceptions.RuntimeIOTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShellyConnectorFactory {

    @Autowired
    private ShellyV1ConnectorImpl v1Connector;

    @Autowired
    private ShellyV2ConnectorImpl v2Connector;

    @Autowired
    private ShellyRegister register;

    public ShellyConnector getConnector(String controllerId, String thingId) {
        var oMeta = register.findShelly(controllerId, thingId);
        return oMeta.map(this::getConnector).orElseThrow(() -> new RuntimeIOTException("Could not create Shelly connector"));
    }

    public ShellyConnector getConnector(ShellyMetadata shellyMetadata) {
        if (shellyMetadata.getVersion() == ShellyMetadata.SHELLY_VERSION.V1) {
            return v1Connector;
        } else {
            return v2Connector;
        }
    }
}
