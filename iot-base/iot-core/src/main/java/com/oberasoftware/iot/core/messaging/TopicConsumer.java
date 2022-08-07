package com.oberasoftware.iot.core.messaging;

public interface TopicConsumer<T> {
    void receive(T message);
}