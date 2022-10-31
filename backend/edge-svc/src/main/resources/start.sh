#!/usr/bin/env bash

echo "Waiting for $rmq_host to be up and running"
while true; do
    nc -q 1 -w 5 $rmq_host 5672 2>/dev/null && break
done

echo "Waiting for $mqtthost to be up and running on port $mqttport"
while true; do
    nc -q 1 -w 5 $mqtthost 1883 2>/dev/null && break
done


echo "MQTT is up and running"

java -Djava.security.egd=file:/dev/./urandom -jar /edge-svc.jar