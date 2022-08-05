package com.oberasoftware.home.client.api;


import com.oberasoftware.iot.core.model.ValueTransportMessage;

/**
 * @author Renze de Vries
 */
public interface StateServiceListener {
    void receive(ValueTransportMessage state);
}
