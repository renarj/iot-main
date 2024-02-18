package com.oberasoftware.home.storage.jpa;

import com.oberasoftware.iot.core.model.storage.impl.ThingSchemaImpl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemaRepository extends CrudRepository<ThingSchemaImpl, String> {
}
