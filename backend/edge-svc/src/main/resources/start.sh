#!/usr/bin/env bash

echo "Waiting for $amq_host to be up and running"
while true; do
    nc -q 1 -w 5 $amq_host 61616 2>/dev/null && break
done

echo "Waiting for $mqtt_host to be up and running on port $mqtt_port"
while true; do
    nc -q 1 -w 5 $mqtt_host 1883 2>/dev/null && break
done


echo "MQTT is up and running"

java -Djava.security.egd=file:/dev/./urandom -jar /edge-svc.jar