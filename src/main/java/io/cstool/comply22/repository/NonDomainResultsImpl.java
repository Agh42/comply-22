package io.cstool.comply22.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import static io.cstool.comply22.adapter.TemporalFieldConverter.toGraphTime;

@Slf4j
public class NonDomainResultsImpl implements NonDomainResults {

    private final Neo4jClient neo4jClient;

    NonDomainResultsImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public Collection<String> findLabelsForNode(Long perpetualId) {
        Map<String, Object> params = Map.of("perpetualId", perpetualId);
        var labels = this.neo4jClient
                .query("" +
                        "MATCH (a:Entity) " +
                        "WHERE id(a) = $perpetualId " +
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
        var resultSummary = this.neo4jClient
                .query(query)
                .bindAll(params)
                .run();
        log.debug(resultSummary.toString());
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

    /**
     * <ul>
     * <li>Find most recent version of entity in the given timeline.
     * <li>Set the "validUntil" timestamp of its pointer to 'now'.
     * <li>Set "validFrom" on the pointer of the new version to the same 'now'.
     * <li>Then move the current-pointer of the entity to the new version.
     * </ul>
     */
    @Transactional()
    public void mergeVersionWithEntity(String reality, Long perpetualEntityId, Long newVersionId, Instant timestamp) {
        this.execute(
                "MATCH p = (e:Entity)-[c:CURRENT]->(old:Version)-[:RECORDED_ON|NEXT|TIP_OF*]->(r:Reality{name:$reality}) " +
                        "WHERE id(e) = $perpetualEntityId " +
                        "WITH e,c,old " +
                        "MATCH (nv:Version) WHERE id(nv) = $newVersionId " +
                        "DELETE c " +
                        "SET old.until = $timestamp " +
                        "SET nv.from = $timestamp " +
                        "MERGE (e)-[:CURRENT]->(nv) ",
                Map.of(
                        "reality", reality,
                        "perpetualEntityId", perpetualEntityId,
                        "newVersionId", newVersionId,
                        "timestamp", toGraphTime(timestamp)
                ));
    }

    @Override
    public void mergeNewVersionWithEntity(Long perpetualEntityId, Long newVersionId) {
        this.execute("MATCH (v:Version)-[:VERSION_OF]->(e:Entity) " +
                        "WHERE id(v) = $newVersionId AND id(e) = $perpetualEntityId " +
                        "MERGE (e)-[:CURRENT]->(v)",
                Map.of(
                        "perpetualEntityId", perpetualEntityId,
                        "newVersionId", newVersionId
                ));
    }

    @Override
    public void initializeTimeline(String timeline, String changeType, Instant timestamp) {
        this.execute("MERGE (r:Reality{name:$timeline})<-[:TIP_OF]-(c:Change{type:$changeType, recorded: $timestamp, transactionTime: $taTimestamp}) " +
                        "WITH r,c " +
                        "MERGE (r)-[:BEGINS_WITH]->(c)",
                Map.of("timeline", timeline,
                        "changeType", changeType,
                        "timestamp", toGraphTime(timestamp),
                        "taTimestamp", toGraphTime(Instant.now())
                )
        );
    }

}
