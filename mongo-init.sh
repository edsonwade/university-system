#!/bin/bash

# Wait for mongo-config to be ready
echo "Waiting for mongo-config..."
until mongosh --host mongo-config --port 27017 --eval "db.runCommand('ping').ok" &>/dev/null; do
  sleep 2
done

echo "Initializing configRS..."
mongosh --host mongo-config --port 27017 --eval "rs.initiate({_id: 'configRS', configsvr: true, members: [{_id: 0, host: 'mongo-config:27017'}]})"

# Wait for mongo-shard1 to be ready
echo "Waiting for mongo-shard1..."
until mongosh --host mongo-shard1 --port 27017 --eval "db.runCommand('ping').ok" &>/dev/null; do
  sleep 2
done

echo "Initializing shard1RS..."
mongosh --host mongo-shard1 --port 27017 --eval "rs.initiate({_id: 'shard1RS', members: [{_id: 0, host: 'mongo-shard1:27017'}]})"

# Wait for mongo-router to be ready
echo "Waiting for mongo-router..."
until mongosh --host mongo-router --port 27017 --eval "db.runCommand('ping').ok" &>/dev/null; do
  sleep 2
done

echo "Adding shard1RS to mongo-router..."
mongosh --host mongo-router --port 27017 --eval "sh.addShard('shard1RS/mongo-shard1:27017')"

echo "MongoDB initialization complete!"
