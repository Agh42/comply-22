# 1. Import HIGH profile


WITH "https://raw.githubusercontent.com/usnistgov/oscal-content/master/nist.gov/SP800-53/rev5/json/NIST_SP-800-53_rev5_HIGH-baseline_profile.json" AS url
CALL apoc.load.json(url, '$.profile') YIELD value
UNWIND value AS profile

MERGE (p:Profile{name:"HIGH"})
SET p.title = profile.metadata.title
SET p.oscalVersion = profile.metadata.`oscal-version` 
SET p.uuid = profile.uuid
SET p.layer = 'Profile'
WITH p, profile

UNWIND profile.imports[0].`include-controls`[0].`with-ids` AS control
MATCH (c1:rev5Control {id: control})
MERGE (p)-[:INCLUDES_CONTROL]->(c1);


# 2. Import MODERATE profile

WITH "https://raw.githubusercontent.com/usnistgov/oscal-content/master/nist.gov/SP800-53/rev5/json/NIST_SP-800-53_rev5_MODERATE-baseline_profile.json" AS url
CALL apoc.load.json(url, '$.profile') YIELD value
UNWIND value AS profile

MERGE (p:Profile{name:"MODERATE"})
SET p.title = profile.metadata.title
SET p.oscalVersion = profile.metadata.`oscal-version` 
SET p.uuid = profile.uuid
SET p.layer = 'Profile'
WITH p, profile

UNWIND profile.imports[0].`include-controls`[0].`with-ids` AS control
MATCH (c1:rev5Control {id: control})
MERGE (p)-[:INCLUDES_CONTROL]->(c1);


# 3. Import LOW profile

WITH "https://raw.githubusercontent.com/usnistgov/oscal-content/master/nist.gov/SP800-53/rev5/json/NIST_SP-800-53_rev5_LOW-baseline_profile.json" AS url
CALL apoc.load.json(url, '$.profile') YIELD value
UNWIND value AS profile

MERGE (p:Profile{name:"LOW"})
SET p.title = profile.metadata.title
SET p.oscalVersion = profile.metadata.`oscal-version` 
SET p.uuid = profile.uuid
SET p.layer = 'Profile'
WITH p, profile

UNWIND profile.imports[0].`include-controls`[0].`with-ids` AS control
MATCH (c1:rev5Control {id: control})
MERGE (p)-[:INCLUDES_CONTROL]->(c1);


# 4. Import PRIVACY profile

WITH "https://raw.githubusercontent.com/usnistgov/oscal-content/master/nist.gov/SP800-53/rev5/json/NIST_SP-800-53_rev5_PRIVACY-baseline_profile.json" AS url
CALL apoc.load.json(url, '$.profile') YIELD value
UNWIND value AS profile

MERGE (p:Profile{name:"PRIVACY"})
SET p.title = profile.metadata.title
SET p.oscalVersion = profile.metadata.`oscal-version` 
SET p.uuid = profile.uuid
SET p.layer = 'Profile'
WITH p, profile

UNWIND profile.imports[0].`include-controls`[0].`with-ids` AS control
MATCH (c1:rev5Control {id: control})
MERGE (p)-[:INCLUDES_CONTROL]->(c1);


## 5 Import Fedramp HIGH profile:

# 5.1 Link controls:

WITH "https://raw.githubusercontent.com/usnistgov/oscal-content/master/fedramp.gov/json/FedRAMP_HIGH-baseline_profile.json" AS url
CALL apoc.load.json(url, '$.profile') YIELD value
UNWIND value AS profile

MERGE (p:Profile{name:"FedRAMP_HIGH"})
SET p.title = profile.metadata.title
SET p.oscalVersion = profile.metadata.`oscal-version` 
SET p.uuid = profile.uuid
SET p.layer = 'Profile'
WITH p, profile

UNWIND profile.imports[0].`include-controls`[0].`with-ids` AS control
MATCH (c1:rev5Control {id: control})
MERGE (p)-[:INCLUDES_CONTROL]->(c1);


# 5.2 Load contraints and addition-alterations:

WITH "https://raw.githubusercontent.com/usnistgov/oscal-content/master/fedramp.gov/json/FedRAMP_HIGH-baseline_profile.json" AS url
CALL apoc.load.json(url, '$.profile') YIELD value
UNWIND value AS profile

UNWIND keys(profile.modify.`set-parameters`) AS setparam
MATCH (pa:ControlParam) WHERE pa.id = setparam
MATCH (pr:Profile{name:"FedRAMP_HIGH"})

MERGE (pr)-[:SET_PARAM]->(sp:SetParam)-[:CONSTRAINS]->(pa)
SET sp.id = setparam
SET sp.layer = 'Profile'
SET sp.constraint = profile.modify.`set-parameters`[setparam].constraints[0].description
SET sp.guideline = profile.modify.`set-parameters`[setparam].guidelines[0].prose

WITH profile,pr

UNWIND profile.modify.alters AS alteration
MATCH (cta:rev5Control)
WHERE cta.id = alteration.`control-id`

UNWIND alteration.adds AS addition
MERGE (pr)-[:ALTERATION]->(add:Addition)-[:ADD_TO]->(cta)
SET add.idRef = addition.`id-ref`
SET add.position = addition.position

WITH alteration,addition,add
UNWIND addition.props AS prop
MERGE (add)-[:ADD_PROP]->(pro:Property)
SET pro.name = prop.name
SET pro.uuid = prop.uuid
SET pro.ns = prop.ns
SET pro.value = prop.value
SET pro.class = prop.class
SET pro.remarks = prop.remarks;


# 5.3 Load removal alterations:

WITH "https://raw.githubusercontent.com/usnistgov/oscal-content/master/fedramp.gov/json/FedRAMP_HIGH-baseline_profile.json" AS url
CALL apoc.load.json(url, '$.profile') YIELD value
UNWIND value AS profile

UNWIND keys(profile.modify.`set-parameters`) AS setparam
MATCH (pa:ControlParam) WHERE pa.id = setparam
MATCH (pr:Profile{name:"FedRAMP_HIGH"})

UNWIND profile.modify.alters AS alteration
MATCH (cta:rev5Control)
WHERE cta.id = alteration.`control-id`

WITH alteration
UNWIND alteration.removes AS removal
MERGE (pr)-[:ALTERATION]->(remove:Removal)-[:REMOVE_FROM]->(ctr)
SET remove.idRef = removal.`id-ref`
SET remove.position = removal.position

WITH removal,remove
UNWIND removal.props AS prop2
MERGE (remove)-[:REMOVE_PROP]->(pro2:Property)
SET pro2.name = prop2.name
SET pro2.uuid = prop2.uuid
SET pro2.ns = prop2.ns
SET pro2.value = prop2.value
SET pro2.class = prop2.class
SET pro2.remarks = prop2.remarks;


# done: alteration, adds props
# todo: load alteration, adds, parts (i.e. 2.10)
