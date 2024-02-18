package com.oberasoftware.home.storage.jpa;

import com.oberasoftware.iot.core.model.storage.impl.PluginImpl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PluginRepository extends CrudRepository<PluginImpl, String> {
    PluginImpl findByPluginId(String pluginId);
}
