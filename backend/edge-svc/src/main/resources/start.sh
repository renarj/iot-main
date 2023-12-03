#!/usr/bin/env bash

echo "Waiting for RMQ $rmq_host to be up and running on port $rmq_port"
while true; do
    nc -q 1 -w 5 $rmq_host $rmq_port 2>/dev/null && break
done

echo "Waiting for $mqtthost to be up and running on port $mqttport"
while true; do
    nc -q 1 -w 5 $mqtthost $mqttport 2>/dev/null && break
done


echo "MQTT is up and running"

java -Djava.security.egd=file:/dev/./urandom -jar /edge-svc.jar