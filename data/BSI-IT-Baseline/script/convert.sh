
# prepare relations:
xq '."ns3:syncRequest".syncData.syncLink[]| [{from: .dependant, to: .dependency, linkType: .relationId}]' src/verinice.xml > build/rels.json

# convert xml to json:
cat src/verinice.xml | xq . > build/verinice.json

# prepare nodes:
jq '.. | .children? | select(.) | .[] | .extId? as $extid | .extObjectType as $type | .syncAttribute? |  map(select(.name|contains("_name"))|.value)[0] as $name | map(select(.name|contains("browser_content"))|.value)[0] as $text | map(select(.name|contains("_id"))|.value)[0] as $id | [{name: $name, text: $text, gsid: $id, extId: $extid, type: $type}]  ' build/verinice.json >  build/nodes.json 

