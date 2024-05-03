package com.oberasoftware.home.web.rest;

import com.oberasoftware.iot.core.legacymodel.DataPoint;
import com.oberasoftware.iot.core.managers.TimeSeriesStore;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@RestController
@RequestMapping("/timeseries")
public class TimeSeriesRestController {
    private static final Logger LOG = getLogger(TimeSeriesRestController.class);

    private static final int DEFAULT_TIME_SCALE = 6;


    @Autowired(required = false)
    private TimeSeriesStore timeSeriesStore;

    @RequestMapping("/controller({controllerId})/things({thingId})/attribute({attribute})")
    public List<DataPoint> findDeviceData(@PathVariable String controllerId, @PathVariable String thingId,
                                          @PathVariable String attribute) {
        return findDeviceData(controllerId, thingId, attribute, "minute", DEFAULT_TIME_SCALE);
    }


    @RequestMapping("/controller({controllerId})/things({thingId})/attribute({attribute})/grouping({group})/hours({time})")
    public List<DataPoint> findDeviceData(@PathVariable String controllerId, @PathVariable String thingId,
                                          @PathVariable String attribute, @PathVariable String group, @PathVariable long time) {
        if(timeSeriesStore != null) {
            TimeSeriesStore.GROUPING grouping = TimeSeriesStore.GROUPING.fromName(group);
            LOG.debug("Doing item: {} time series request for: {} hours grouped by: {}", thingId, time, grouping);

            return timeSeriesStore.findDataPoints(controllerId, thingId, attribute, grouping,
                    time, TimeUnit.HOURS);
        }
        return new ArrayList<>();
    }

}
