package io.cstool.comply22.adapter;

import org.neo4j.driver.Value;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Converts types between entities and OGM driver where
 * necessary.
 *
 */
public class TemporalFieldConverter {

    public static ZonedDateTime toGraphTime(Instant timestamp) {
        return ZonedDateTime.ofInstant(timestamp,
                ZoneId.of("Z"));
    }

    public Instant toInstant(Value temporalValue) {
        return temporalValue.asLocalDateTime()
                .toInstant(ZoneOffset.of("Z"));
    }
}
