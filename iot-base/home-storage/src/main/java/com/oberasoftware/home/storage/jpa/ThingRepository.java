package com.oberasoftware.home.storage.jpa;

import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThingRepository extends CrudRepository<IotThingImpl, String> {

    IotThingImpl findByThingIdAndControllerId(String thingId, String controllerId);

    List<IotThingImpl> findByControllerId(String controllerId);

    List<IotThingImpl> findByControllerIdAndPluginId(String controllerId, String pluginId);

    List<IotThingImpl> findByControllerIdAndParentId(String controllerId, String parentId);

    List<IotThingImpl> findByControllerIdAndPluginIdAndType(String controllerId, String parentId, String type);

    List<IotThingImpl> findByControllerIdAndPluginIdAndSchemaId(String controllerId, String parentId, String type);
}
