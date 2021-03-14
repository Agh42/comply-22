#!/bin/sh

docker run \
--publish=7474:7474 \
--publish=7687:7687 \
--volume=$HOME/neo4j/data:/data \
-e 'NEO4J_AUTH=neo4j/secret' \
-e 'NEO4J_dbms_allow__upgrade=true' \
--user="$(id -u):$(id -g)" \
--name=comply22-neo4j \
neo4j:4.2.3

