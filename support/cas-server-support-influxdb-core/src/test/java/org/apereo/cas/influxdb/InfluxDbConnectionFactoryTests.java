package org.apereo.cas.influxdb;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.junit.ConditionalIgnore;
import org.apereo.cas.util.junit.ConditionalSpringRunner;
import org.apereo.cas.util.junit.RunningContinuousIntegrationCondition;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * This is {@link InfluxDbConnectionFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@SpringBootTest(classes = RefreshAutoConfiguration.class)
@RunWith(ConditionalSpringRunner.class)
@ConditionalIgnore(condition = RunningContinuousIntegrationCondition.class)
public class InfluxDbConnectionFactoryTests {
    private static final String CAS_EVENTS_DATABASE = "casEventsDatabase";
    private InfluxDbConnectionFactory factory;

    @Before
    public void init() {
        this.factory = new InfluxDbConnectionFactory("http://localhost:8086", "root",
            "root", CAS_EVENTS_DATABASE, true);
    }

    @After
    public void shutdown() {
        this.factory.close();
    }

    @Test
    public void verifyWritePoint() {
        final Point p = Point.measurement("events")
            .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .addField("hostname", "cas.example.org")
            .build();
        factory.write(p, CAS_EVENTS_DATABASE);
        final QueryResult result = factory.query("*", "events", CAS_EVENTS_DATABASE);
        final InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        final List<InfluxEvent> resultEvents = resultMapper.toPOJO(result, InfluxEvent.class);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("cas.example.org", resultEvents.iterator().next().hostname);
    }

    @Measurement(name = "events")
    public static class InfluxEvent {
        @Column(name = "time")
        private Instant time;

        @Column(name = "hostname", tag = true)
        private String hostname;
    }
}
