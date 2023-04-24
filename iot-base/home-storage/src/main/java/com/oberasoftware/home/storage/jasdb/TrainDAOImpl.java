package com.oberasoftware.home.storage.jasdb;

import com.google.common.collect.ImmutableMap;
import com.oberasoftware.iot.core.storage.TrainDAO;
import com.oberasoftware.iot.core.train.model.Locomotive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class TrainDAOImpl extends BaseDAO implements TrainDAO {
    private static final Logger LOG = LoggerFactory.getLogger(TrainDAOImpl.class);

    @Override
    public List<Locomotive> findLocs(String controllerId) {
        return newArrayList(findItems(Locomotive.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .build()));
    }

    @Override
    public List<Locomotive> findAllLocs() {
        return newArrayList(findItems(Locomotive.class, new ImmutableMap.Builder<String, String>()
                .build()));
    }

    @Override
    public Optional<Locomotive> findLoc(String controllerId, String locId) {
        Locomotive loc = findItem(Locomotive.class, new ImmutableMap.Builder<String, String>()
                .put("controllerId", controllerId)
                .put("thingId", locId)
                .build());

        return Optional.ofNullable(loc);
    }
}
