package com.oberasoftware.robo.maximus.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oberasoftware.jasdb.api.entitymapper.EntityManager;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.api.session.query.QueryBuilder;
import com.oberasoftware.robo.api.exceptions.RoboException;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.core.motion.MotionImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class JasDBMotionStorageImpl implements MotionStorage {
    private static final Logger LOG = getLogger(JasDBMotionStorageImpl.class);

    @Autowired
    private JasDBSessionFactory sessionFactory;

    @Override
    public void storeMotion(String motionName, Motion motion) {
        SimpleMotionEntity existingMotion = findMotionEntity(motionName);
        String id = existingMotion != null ? existingMotion.getId() : null;

        try {
            SimpleMotionEntity entity = new SimpleMotionEntity(id, motionName, toBlob(motion));
            DBSession session = sessionFactory.createSession();
            EntityManager em = session.getEntityManager();
            em.persist(entity);
        } catch (JasDBException e) {
            LOG.error("Could not persist motion");
        }
    }

    @Override
    public void deleteMotion(String motionName) {
        SimpleMotionEntity existingMotion = findMotionEntity(motionName);
        if(existingMotion != null) {
            try {
                DBSession session = sessionFactory.createSession();
                session.getEntityManager().remove(existingMotion);
            } catch (JasDBException e) {
                LOG.error("Could not remove motion with name: " + motionName);
            }
        }
    }

    @Override
    public List<Motion> findAllMotions() {
        try {
            DBSession session = sessionFactory.createSession();
            EntityManager em = session.getEntityManager();

            List<SimpleMotionEntity> motionList = em.findEntities(SimpleMotionEntity.class, QueryBuilder.createBuilder());

            return motionList.stream().map(m -> fromBlob(m.getBlob())).collect(Collectors.toList());
        } catch (JasDBException e) {
            LOG.error("Could not load all motions", e);
        }

        return Collections.emptyList();
    }

    private SimpleMotionEntity findMotionEntity(String motionName) {
        try {
            DBSession session = sessionFactory.createSession();
            EntityManager em = session.getEntityManager();
            List<SimpleMotionEntity> motionsList = em.findEntities(SimpleMotionEntity.class, QueryBuilder.createBuilder().field("name").value(motionName));

            if(motionsList.size() == 1) {
                return motionsList.get(0);
            }
        } catch(JasDBException e) {
            LOG.error("Could not load motion", e);
        }
        return null;
    }

    @Override
    public Motion findMotion(String motionId) {
        SimpleMotionEntity e = findMotionEntity(motionId);
        if(e != null) {
            return fromBlob(e.getBlob());
        } else {
            return null;
        }
    }

    private Motion fromBlob(String blob) {
        LOG.info("Raw blob from storage: {}", blob);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(blob, MotionImpl.class);
        } catch (IOException e) {
            throw new RoboException("Could not load the motion from json", e);
        }

//        GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapter(KeyFrame.class, (InstanceCreator<KeyFrame>) type -> new KeyFrameImpl());
//        Gson gson = builder.create();
//
//
//        return gson.fromJson(blob, MotionImpl.class);
    }

    private String toBlob(Motion motion) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(motion);
        } catch (JsonProcessingException e) {
            throw new RoboException("Could not serialise Motion format", e);
        }

//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        return gson.toJson(motion);
    }
}
