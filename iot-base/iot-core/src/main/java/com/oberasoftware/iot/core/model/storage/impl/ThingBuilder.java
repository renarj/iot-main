package com.oberasoftware.iot.core.model.storage.impl;

import com.oberasoftware.iot.core.model.IotThing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ThingBuilder {

    private IotThingImpl thing;

    private Map<String, String> properties = new HashMap<>();

    private ThingBuilder(String thingId, String controllerId) {
        this.thing = new IotThingImpl(thingId, controllerId);
    }

    public static ThingBuilder create(String thingId, String controllerId) {
        return new ThingBuilder(thingId, controllerId);
    }

    public ThingBuilder friendlyName(String friendlyName) {
        this.thing.setFriendlyName(friendlyName);
        return this;
    }

    public ThingBuilder type(String type) {
        this.thing.setType(type);
        return this;
    }

    public ThingBuilder plugin(String pluginId) {
        this.thing.setPluginId(pluginId);
        return this;
    }

    public ThingBuilder schema(String schemaId) {
        this.thing.setSchemaId(schemaId);
        return this;
    }

    public ThingBuilder parent(String parentId) {
        this.thing.setParentId(parentId);
        return this;
    }

    public ThingBuilder parent(IotThing thing) {
        this.thing.setParentId(thing.getThingId());
        return this;
    }

    public ThingBuilder addProperty(String property, String value) {
        this.properties.put(property, value);
        return this;
    }

    public ThingBuilder addAttribute(String attribute, AttributeType type) {
        this.thing.addAttribute(attribute, type);
        return this;
    }

    public ThingBuilder addAttributes(String... attributes) {
        Arrays.stream(attributes).forEach(a -> this.thing.addAttribute(a, AttributeType.LABEL));
        return this;
    }

    public IotThingImpl build() {
        thing.setProperties(properties);
        return thing;
    }
}
