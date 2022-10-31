package com.oberasoftware.iot.core.robotics.events;

/**
 * @author Renze de Vries
 */
public class TextualSensorEvent implements SensorEvent<String> {

    private final String controllerId;
    private final String itemId;
    private final String label;
    private final String text;

    public TextualSensorEvent(String controllerId, String itemId, String label, String text) {
        this.controllerId = controllerId;
        this.itemId = itemId;
        this.label = label;
        this.text = text;
    }

    @Override
    public String getValue() {
        return text;
    }

    @Override
    public String getControllerId() {
        return controllerId;
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public String getAttribute() {
        return label;
    }

    @Override
    public String toString() {
        return "TextualSensorEvent{" +
                "controllerId='" + controllerId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", label='" + label + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
