#!/usr/bin/env bash

echo "Waiting for $rmq_host to be up and running test-3"
while true; do
    nc -q 1 -w 5 $rmq_host 5672 </dev/null && break
done

echo "RMQ is up and running"

#echo "Waiting for $cassandra_host to be up and running"
#while true; do
#    nc -q 1 -w 5 $cassandra_host 9042 2>/dev/null && break
#done
#
#echo "Cassandra is up and running"

java -Djava.security.egd=file:/dev/./urandom -jar /state-svc.jar