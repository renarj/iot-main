package com.oberasoftware.iot.core.messaging;

public interface TopicListener<T> {
    void connect();

    void close();

    void register(String topic, TopicConsumer<T> topicConsumer);
}