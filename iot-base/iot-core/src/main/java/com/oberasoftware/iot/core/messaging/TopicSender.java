package com.oberasoftware.iot.core.messaging;

public interface TopicSender<T> {
    void connect();

    void close();

    void publish(String topic, T message);
}