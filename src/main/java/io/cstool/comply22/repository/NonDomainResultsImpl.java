package io.cstool.comply22.repository;

import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Collection;
import java.util.Map;

public class NonDomainResultsImpl implements NonDomainResults {

    private final Neo4jClient neo4jClient;

    NonDomainResultsImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public Collection<String> findLabelsForNode(Long id) {
        Map<String, Object> params = Map.of("id", id);
        var labels = this.neo4jClient
                .query("" +
                        "MATCH (a:Entity) " +
                        "WHERE id(a) = $id " +
                        "RETURN labels(a) as labels"
                )
                .bindAll(params)
//                .fetchAs(String.class)
//                .mappedBy((typeSystem, record) -> {
//                    return record.get("labels").asString();
//                })
                .fetch()
                .first()
                .orElseThrow()
                .get("labels");
        return (Collection<String>) labels;
    }

    private void execute(String query, Map<String, Object> params) {
        this.neo4jClient
                .query(query)
                .bindAll(params)
                .fetch();
    }

    @Override
    public void deleteAllByLabel(String label) {
        Map<String, Object> params = Map.of("label", label);
        neo4jClient.query("MATCH (a:Entity) WHERE $label IN labels(a) DETACH DELETE a")
                .bindAll(params)
                .run();
    }

    /**
     * Point reality tip to the new change. Link previous tip to new tip.
     */
    public void mergeWithTimeline(String timeline, Long changeId) {
        this.execute("MATCH (r:Reality{name:$timeline})<-[oto:TIP_OF]-(oldtip:Change) " +
                        "WITH r,oto,oldtip " +
                        "DELETE oto " +
                        "WITH r,oldtip " +
                        "MATCH (newtip:Change) " +
                        "WHERE id(newtip) = $changeId " +
                        "WITH r,oldtip,newtip " +
                        "MERGE (oldtip)-[:NEXT]->(newtip)-[:TIP_OF]->(r) ",
                Map.of("timeline", timeline,
                        "changeId", changeId));
    }

}
