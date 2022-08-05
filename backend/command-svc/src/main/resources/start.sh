#!/usr/bin/env bash

echo "Waiting for $amq_host to be up and running"
while true; do
    nc -q 1 -w 5 $amq_host 61616 2>/dev/null && break
done

echo "AMQ is up and running"

java -Djava.security.egd=file:/dev/./urandom -jar command-svc.jar