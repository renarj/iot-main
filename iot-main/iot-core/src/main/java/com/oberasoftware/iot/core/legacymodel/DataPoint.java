package com.oberasoftware.iot.core.legacymodel;

/**
 * @author renarj
 */
public interface DataPoint {
    double getTimestamp();

    String getItemId();

    String getLabel();

    double getValue();
}
