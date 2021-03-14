# Import data from a DB dump:
docker run --interactive --tty --rm \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    --volume=$HOME/neo4j/backups:/backups \
    --name=comply22-neo4j \
    -e 'NEO4J_AUTH=neo4j/secret' \
    --user="$(id -u):$(id -g)" \
    neo4j:4.2.3 \
neo4j-admin load --from=/backups/dump.db --database=graph.db --force


