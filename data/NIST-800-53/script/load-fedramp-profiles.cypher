
// 5 Import Fedramp HIGH profile (for 800-53 rev4):

// 5.1 Link controls:

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


// 5.2 Load:
// - constraints 
// - alterations: 
// - add props
// - add parts (with props and subparts)

WITH "https://raw.githubusercontent.com/GSA/fedramp-automation/master/baselines/rev4/json/FedRAMP_rev4_HIGH-baseline_profile.json" AS url
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
MERGE (add)-[:ADD_PROP]->(propNode:Property)
SET propNode.name = prop.name
SET propNode.uuid = prop.uuid
SET propNode.ns = prop.ns
SET propNode.value = prop.value
SET propNode.class = prop.class
SET propNode.remarks = prop.remarks

WITH alteration,addition,add
UNWIND addition.parts AS part
MERGE (add)-[:ADD_PART]->(partNode:Part)
SET partNode.id = part.id
SET partNode.name = part.name
SET partNode.title = part.title
SET partNode.prose = part.prose

WITH alteration,addition,add,part,partNode
UNWIND part.props AS partProp
MERGE (partNode)-[:HAS_PROP]->(partPropNode:Property)
SET partPropNode.name = partProp.name
SET partPropNode.value = partProp.value

WITH alteration,addition,add,part,partNode
UNWIND part.parts AS partL2
MERGE (partNode)-[:HAS_PART]->(partNodeL2:Part)
SET partNodeL2.id = partL2.id
SET partNodeL2.name = partL2.name
SET partNodeL2.prose = partL2.prose

WITH alteration,addition,add,part,partNode,partL2,partNodeL2
UNWIND partL2.props AS partL2Prop
MERGE (partNodeL2)-[:HAS_PROP]->(partL2PropNode:Property)
SET partL2PropNode.name = partL2Prop.name
SET partL2PropNode.value = partL2Prop.value;


// 5.3 Load alterations: remove props

WITH "https://raw.githubusercontent.com/GSA/fedramp-automation/master/baselines/rev4/json/FedRAMP_rev4_HIGH-baseline_profile.json" AS url
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

