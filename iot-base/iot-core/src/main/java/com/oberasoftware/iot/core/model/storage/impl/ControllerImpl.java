package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.Controller;
import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.util.Map;

/**
 * @author renarj
 */
@Entity(name="Controllers")
@JasDBEntity(bagName = "Controllers")
public class ControllerImpl implements Controller {
    @Column(unique = true)
    private String controllerId;

    @jakarta.persistence.Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String orgId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ControllerProperties")
    private Map<String, String> properties;

    public ControllerImpl(String id, String controllerId, String orgId, Map<String, String> properties) {
        this.id = id;
        this.controllerId = controllerId;
        this.orgId = orgId;
        this.properties = properties;
    }

    public ControllerImpl(String controllerId, String orgId, Map<String, String> properties) {
        this.controllerId = controllerId;
        this.orgId = orgId;
        this.properties = properties;
    }


    public ControllerImpl() {
    }

    @Override
    @Id
    @JasDBProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    @JasDBProperty
    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    @Override
    @JasDBProperty
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    @JasDBProperty
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "ControllerItem{" +
                ", controllerId='" + controllerId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
