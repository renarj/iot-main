package com.oberasoftware.robo.maximus.storage;

import com.oberasoftware.jasdb.api.entitymapper.annotations.Id;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBEntity;
import com.oberasoftware.jasdb.api.entitymapper.annotations.JasDBProperty;

@JasDBEntity(bagName = "motions")
public class SimpleMotionEntity {
    private String id;
    private String name;
    private String blob;

    public SimpleMotionEntity(String name, String blob) {
        this.name = name;
        this.blob = blob;
    }

    public SimpleMotionEntity(String id, String name, String blob) {
        this.id = id;
        this.name = name;
        this.blob = blob;
    }

    public SimpleMotionEntity() {
    }

    @Id
    @JasDBProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JasDBProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JasDBProperty
    public String getBlob() {
        return blob;
    }

    public void setBlob(String blob) {
        this.blob = blob;
    }

    @Override
    public String toString() {
        return "SimpleMotionEntity{" +
                "name='" + name + '\'' +
                ", blob='" + blob + '\'' +
                '}';
    }
}
