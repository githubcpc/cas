package org.apereo.cas.adaptors.ldap.services;

import org.apereo.cas.adaptors.ldap.LdapIntegrationTestsOperations;
import org.apereo.cas.util.junit.ConditionalIgnore;
import org.apereo.cas.util.junit.ConditionalSpringRunner;
import org.apereo.cas.util.junit.RunningStandaloneCondition;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;

/**
 * Unit test for {@link LdapServiceRegistry} class.
 *
 * @author Misagh Moayyed
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@TestPropertySource(locations = "classpath:/ldapsvc.properties")
@RunWith(ConditionalSpringRunner.class)
@ConditionalIgnore(condition = RunningStandaloneCondition.class)
public class LdapServiceRegistryTests extends BaseLdapServiceRegistryTests {

    @BeforeClass
    public static void bootstrap() throws Exception {
        LdapIntegrationTestsOperations.initDirectoryServer(1390);
    }
}
