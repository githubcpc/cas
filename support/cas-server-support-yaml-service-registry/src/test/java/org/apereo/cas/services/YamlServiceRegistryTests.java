package org.apereo.cas.services;

import static org.mockito.Mockito.*;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.services.replication.NoOpRegisteredServiceReplicationStrategy;
import org.apereo.cas.services.resource.DefaultRegisteredServiceResourceNamingStrategy;
import org.junit.Before;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Test cases for {@link YamlServiceRegistry}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class YamlServiceRegistryTests extends AbstractResourceBasedServiceRegistryTests {

    @Before
    public void setup() {
        try {
            this.dao = new YamlServiceRegistry(RESOURCE, false,
                    mock(ApplicationEventPublisher.class),
                    new NoOpRegisteredServiceReplicationStrategy(),
                    new DefaultRegisteredServiceResourceNamingStrategy());
        } catch (final Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
