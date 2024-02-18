package com.oberasoftware.home.storage.jpa;

import com.oberasoftware.iot.core.model.storage.impl.ControllerImpl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControllerRepository extends CrudRepository<ControllerImpl, String> {
    ControllerImpl findByControllerId(String controllerId);
}
