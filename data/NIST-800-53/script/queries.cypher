# Some example queries:

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

# find controls most required by other controls:
MATCH (c1)<-[r:RELATED{type:'required'}]-(c2) 
RETURN c1.id, c1.title, count(r) as numRequired
ORDER by numRequired DESC
LIMIT 100;

# find all enhancements, parts and params for one control:
MATCH p=(c:rev5Control)<-[r:IS_ENHANCEMENT_OF*]-(c2:rev5Control)-[:HAS_PART*]->(part)
WHERE c.id='ac-2'
WITH p, c2
MATCH p2=(c2)-[:HAS_PARAM]->(param)
RETURN p,p2 limit 1000;


# list control with enhancements, parts and params for those parts:
# (expand ac2.5 to also see working link to ac-11 fro within the prose)
MATCH p=(c:rev5Control)<-[r:IS_ENHANCEMENT_OF*]-(e:rev5Control)-[:HAS_PART*]->(part)
WHERE c.id='ac-2'
WITH e, c, part
MATCH p2=(e)-[:HAS_PARAM]->(param)
RETURN c.id, e.id, e.title, part.id, part.prose, param.id, param.label
ORDER BY c.id, e.id, part.id, param.id
LIMIT 1000;




