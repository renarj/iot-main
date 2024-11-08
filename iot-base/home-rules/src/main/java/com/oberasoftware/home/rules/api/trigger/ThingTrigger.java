package com.oberasoftware.home.rules.api.trigger;

/**
 * @author Renze de Vries
 */
public class ThingTrigger implements Trigger {
    public enum TRIGGER_TYPE {
        THING_STATE_CHANGE,
        THING_EVENT
    }

    private TRIGGER_TYPE triggerType;

    public ThingTrigger(TRIGGER_TYPE triggerType) {
        this.triggerType = triggerType;
    }

    public ThingTrigger() {
    }

    public TRIGGER_TYPE getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TRIGGER_TYPE triggerType) {
        this.triggerType = triggerType;
    }

    @Override
    public String toString() {
        return "DeviceTrigger{" +
                "triggerType=" + triggerType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThingTrigger that = (ThingTrigger) o;

        return triggerType == that.triggerType;

    }

    @Override
    public int hashCode() {
        return triggerType.hashCode();
    }
}
