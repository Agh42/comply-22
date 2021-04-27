
# find top level groups:
MATCH (g:BsiGroup) WHERE NOT (:BsiGroup)-[:CONTAINS]->(g) RETURN g;


