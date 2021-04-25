WITH 'http://some.url/nodes.json' AS url
CALL apoc.load.json(url, '$[?(@.type == "bp_requirement")]') YIELD value
UNWIND value AS node

MERGE (r:BsiRequirement {extId: node.extId})
SET r.name = node.name
SET r.text = node.text
SET r.gsid = node.gsid;


WITH 'http://some.url/nodes.json' AS url
CALL apoc.load.json(url, '$[?(@.type == "bp_safeguard")]') YIELD value
UNWIND value AS node

MERGE (r:BsiSafeguard {extId: node.extId})
SET r.name = node.name
SET r.text = node.text
SET r.gsid = node.gsid;


WITH 'http://some.url/nodes.json' AS url
CALL apoc.load.json(url, '$[?(@.type == "bp_threat")]') YIELD value
UNWIND value AS node

MERGE (r:BsiThreat {extId: node.extId})
SET r.name = node.name
SET r.text = node.text
SET r.gsid = node.gsid;


WITH 'http://some.url/nodes.json' AS url
CALL apoc.load.json(url, '$[?(@.type == "bp_requirement_group")]') YIELD value
UNWIND value AS node

MERGE (r:BsiGroup {extId: node.extId})
SET r.name = node.name
SET r.text = node.text
SET r.gsid = node.gsid;


WITH 'http://some.url/nodes.json' AS url
CALL apoc.load.json(url, '$[?(@.type == "bp_safeguard_group")]') YIELD value
UNWIND value AS node

MERGE (r:BsiGroup {extId: node.extId})
SET r.name = node.name
SET r.text = node.text
SET r.gsid = node.gsid;


WITH 'http://some.url/nodes.json' AS url
CALL apoc.load.json(url, '$[?(@.type == "bp_threat_group")]') YIELD value
UNWIND value AS node

MERGE (r:BsiGroup {extId: node.extId})
SET r.name = node.name
SET r.text = node.text
SET r.gsid = node.gsid;


CREATE INDEX g_extid_idx IF NOT EXISTS FOR (n:BsiGroup) ON (n.extId);
CREATE INDEX s_extid_idx IF NOT EXISTS FOR (n:BsiSafeguard) ON (n.extId);
CREATE INDEX r_extid_idx IF NOT EXISTS FOR (n:BsiRequirement) ON (n.extId);
CREATE INDEX t_extid_idx IF NOT EXISTS FOR (n:BsiThreat) ON (n.extId);


# These are an error:
#WITH 'http://some.url/rels.json' AS url
#CALL apoc.load.json(url, '$[?(@.linkType == "rel_bp_requirement_bp_requirement")]') YIELD value
#UNWIND value AS rel
#
#MATCH (n1)
#WHERE n1.extId = rel.from
#MATCH (n2)
#WHERE n2.extId = rel.to
#
#MERGE (n1)-[r:RELATED]->(n2)
#SET r.type = rel.linkType;


WITH 'http://some.url/rels.json' AS url
CALL apoc.load.json(url, '$[?(@.linkType == "rel_bp_requirement_bp_safeguard")]') YIELD value
UNWIND value AS rel

MATCH (n1)
WHERE n1.extId = rel.from
MATCH (n2)
WHERE n2.extId = rel.to

MERGE (n1)-[r:REQUIRED_BY_SAFEGUARD]->(n2);


WITH 'http://some.url/rels.json' AS url
CALL apoc.load.json(url, '$[?(@.linkType == "rel_bp_requirement_bp_threat")]') YIELD value
UNWIND value AS rel

MATCH (n1)
WHERE n1.extId = rel.from
MATCH (n2)
WHERE n2.extId = rel.to

MERGE (n1)-[r:REQUIRED_BY_THREAT]->(n2);

# todo: link groups to their children

