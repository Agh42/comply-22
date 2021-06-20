# Some example queries:

# List controls (322) without Enhancements:
MATCH (c1:rev5Control) 
WHERE not c1:Enhancement
RETURN c1.id, c1.title;

# List enhancements(867), with their controls
# Controls + Enhancements: 1189 total
MATCH (c1:Enhancement)-[:IS_ENHANCEMENT_OF]->(c2)
RETURN c1.id, c1.title, c2.id, c2.title;

# a withdrawn control with "incorporated-into" link 
MATCH (c:rev5Control)-[r:HAS_PROP]->(p:ControlProp{value:'withdrawn'}) 
WHERE c.id = 'sc-12.4'
RETURN c;

# a withdrawn control with "moved-to" link
MATCH (c:rev5Control)-[r:HAS_PROP]->(p:ControlProp{value:'withdrawn'}) 
WHERE c.id = 'at-3.4'
RETURN c;

# find most referenced backmatter:
MATCH (b:Backmatter)<-[r:REFERENCES]-(c) 
RETURN b.title, b.rlinks, count(r) as numReferenced
ORDER by numReferenced DESC
LIMIT 100;

# find controls most referenced by other controls:
MATCH (c1)<-[r:RELATED{type:'related'}]-(c2) 
RETURN c1.id, c1.title, count(r) AS numRequired
ORDER by numRequired DESC
LIMIT 100;

# Lvl 2:
# returns 121 controls
MATCH (c1)<-[rx:RELATED*2{type:'related'}]-(c2)
RETURN c1.id, c1.title, COUNT(rx) AS numRequiredLv2
ORDER by numRequiredLv2 DESC
LIMIT 10;

# Lvl 5:
# (runs approx. 8 min.)
MATCH (c1)<-[rx:RELATED*5{type:'related'}]-(c2)
RETURN c1.id, c1.title, COUNT(rx) AS numRequiredLv5
ORDER by numRequiredLv5 DESC
LIMIT 10;


# find all enhancements, parts and params for one control:
# (expand ac2.5 to also see working link to ac-11 fro within the prose)
MATCH p=(c:rev5Control)<-[r:IS_ENHANCEMENT_OF*]-(c2:rev5Control)-[:HAS_PART*]->(part)
WHERE c.id='ac-2'
WITH p, c2
MATCH p2=(c2)-[:HAS_PARAM]->(param)
RETURN p,p2 limit 1000;


# same, but also show set-param contraints from profiles:
MATCH p=(c:rev5Control)<-[r:IS_ENHANCEMENT_OF*]-(c2:rev5Control)-[:HAS_PART*]->(part)
WHERE c.id='ac-2'
WITH p, c2
MATCH p2=(c2)-[:HAS_PARAM]->(param)<-[:CONSTRAINS]-(sp:SetParam)
RETURN p,p2,sp limit 1000;


# show profile alterations to controls:
MATCH p=(c:rev5Control)<-[r:IS_ENHANCEMENT_OF*]-(c2:rev5Control)-[:HAS_PART*]->(part)
WHERE c.id='ac-2'
WITH p, c2
MATCH p3=(c2)<-[:ADD_TO]-(a:Addition)
RETURN p,p3 limit 1000;



# list control with enhancements, parts and params for those parts:
MATCH p=(c:rev5Control)<-[r:IS_ENHANCEMENT_OF*]-(e:rev5Control)-[:HAS_PART*]->(part)
WHERE c.id='ac-2'
WITH e, c, part
MATCH p2=(e)-[:HAS_PARAM]->(param)
RETURN c.id, e.id, e.title, part.id, part.prose, param.id, param.label
ORDER BY c.id, e.id, part.id, param.id
LIMIT 1000;

# list controls that are included in HIGH profile but not MODERATE:
MATCH (p:Profile{name:"HIGH"})-[:INCLUDES_CONTROL]->(c)
WHERE NOT (c)<-[:INCLUDES_CONTROL]-(:Profile{name:"MODERATE"})
RETURN p, c;


