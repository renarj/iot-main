package com.oberasoftware.robo.maximus.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oberasoftware.jasdb.api.entitymapper.EntityManager;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.api.session.DBSession;
import com.oberasoftware.jasdb.api.session.query.QueryBuilder;
import com.oberasoftware.robo.api.motion.Motion;
import com.oberasoftware.robo.core.motion.MotionImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void storeMotion(String motionId, Motion motion) {
        Motion existingMotion = findMotion(motionId);
        if(existingMotion != null) {

        }

        try {
            DBSession session = sessionFactory.createSession();
            EntityManager em = session.getEntityManager();
            em.persist(new SimpleMotionEntity(motionId, toBlob(motion)));
        } catch (JasDBException e) {
            LOG.error("Could not persist motion");
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

    @Override
    public Motion findMotion(String motionId) {
        try {
            DBSession session = sessionFactory.createSession();
            EntityManager em = session.getEntityManager();
            List<SimpleMotionEntity> motionsList = em.findEntities(SimpleMotionEntity.class, QueryBuilder.createBuilder().field("name").value(motionId));

            if(motionsList.size() == 1) {
                return fromBlob(motionsList.get(0).getBlob());
            }
        } catch(JasDBException e) {
            LOG.error("Could not load motion", e);
        }
        return null;
    }

    private Motion fromBlob(String blob) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(blob, MotionImpl.class);
    }

    private String toBlob(Motion motion) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(motion);
    }
}
