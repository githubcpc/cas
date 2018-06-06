package org.apereo.cas.services;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.JpaServiceRegistryConfiguration;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.util.CollectionUtils;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * Handles tests for {@link JpaServiceRegistry}
 *
 * @author battags
 * @since 3.1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    AopAutoConfiguration.class,
    CasCoreUtilConfiguration.class,
    JpaServiceRegistryConfiguration.class,
    JpaServiceRegistryTests.TimeAwareServicesManagerConfiguration.class,
    CasCoreServicesConfiguration.class})
@DirtiesContext
@Slf4j
public class JpaServiceRegistryTests {

    @Autowired
    @Qualifier("jpaServiceRegistry")
    private ServiceRegistry serviceRegistry;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Before
    public void setUp() {
        final List<RegisteredService> services = this.serviceRegistry.load();
        services.forEach(service -> this.serviceRegistry.delete(service));
    }

    @Test
    public void verifySaveMethodWithNonExistentServiceAndNoAttributes() {
        final RegexRegisteredService r = new RegexRegisteredService();
        r.setName("verifySaveMethodWithNonExistentServiceAndNoAttributes");
        r.setServiceId("testId");
        r.setTheme("theme");
        r.setDescription("description");
        r.setPublicKey(new RegisteredServicePublicKeyImpl("classpath:/test.pub", "RSA"));

        final RegisteredService r2 = this.serviceRegistry.save(r);
        final RegisteredService r3 = this.serviceRegistry.findServiceById(r2.getId());

        assertEquals(r, r2);
        assertEquals(r2, r3);
    }

    @Test
    public void verifySaveAttributeReleasePolicy() {
        final RegexRegisteredService r = new RegexRegisteredService();
        r.setName("verifySaveAttributeReleasePolicy");
        r.setServiceId("testId");
        r.setTheme("theme");
        r.setDescription("description");
        r.setAttributeReleasePolicy(new ReturnAllAttributeReleasePolicy());
        final DefaultRegisteredServiceAccessStrategy strategy = new DefaultRegisteredServiceAccessStrategy();
        strategy.setDelegatedAuthenticationPolicy(
            new DefaultRegisteredServiceDelegatedAuthenticationPolicy(CollectionUtils.wrapList("one", "two")));
        r.setAccessStrategy(strategy);
        final RegisteredService r2 = this.serviceRegistry.save(r);
        final RegisteredService r3 = this.serviceRegistry.findServiceById(r2.getId());

        assertEquals(r, r2);
        assertEquals(r2, r3);
        assertNotNull(r3.getAttributeReleasePolicy());
        assertEquals(r2.getAttributeReleasePolicy(), r3.getAttributeReleasePolicy());
    }

    @Test
    public void verifySaveMethodWithExistingServiceNoAttribute() {
        final RegexRegisteredService r = new RegexRegisteredService();
        r.setName("verifySaveMethodWithExistingServiceNoAttribute");
        r.setServiceId("testId");
        r.setTheme("theme");
        r.setDescription("description");

        this.serviceRegistry.save(r);

        final List<RegisteredService> services = this.serviceRegistry.load();
        final RegisteredService r2 = services.get(0);

        r.setId(r2.getId());
        this.serviceRegistry.save(r);

        final RegisteredService r3 = this.serviceRegistry.findServiceById(r.getId());

        assertEquals(r, r2);
        assertEquals(r.getTheme(), r3.getTheme());
    }

    @Test
    public void verifyRegisteredServiceProperties() {
        final RegexRegisteredService r = new RegexRegisteredService();
        r.setName("test");
        r.setServiceId("testId");
        r.setTheme("theme");
        r.setDescription("description");

        final Map propertyMap = new HashMap<>();

        final DefaultRegisteredServiceProperty property = new DefaultRegisteredServiceProperty();
        final Set<String> values = new HashSet<>();
        values.add("value1");
        values.add("value2");
        property.setValues(values);
        propertyMap.put("field1", property);

        final DefaultRegisteredServiceProperty property2 = new DefaultRegisteredServiceProperty();

        final Set<String> values2 = new HashSet<>();
        values2.add("value1");
        values2.add("value2");
        property2.setValues(values2);
        propertyMap.put("field2", property2);

        r.setProperties(propertyMap);

        this.serviceRegistry.save(r);

        final RegisteredService r2 = this.serviceRegistry.load().get(0);
        assertEquals(2, r2.getProperties().size());

    }


    @Test
    public void verifyRegisteredServiceContacts() {
        final RegexRegisteredService r = new RegexRegisteredService();
        r.setName("testContacts");
        r.setServiceId("testContacts");

        final List contacts = new ArrayList<>();
        final DefaultRegisteredServiceContact contact = new DefaultRegisteredServiceContact();
        contact.setDepartment("department");
        contact.setId(1234);
        contact.setName("ContactName");
        contact.setPhone("123-456-789");
        contacts.add(contact);
        r.setContacts(contacts);

        this.serviceRegistry.save(r);
        final RegisteredService r2 = this.serviceRegistry.load().get(0);
        assertEquals(1, r2.getContacts().size());
    }

    @Test
    public void verifyOAuthServices() {
        final OAuthRegisteredService r = new OAuthRegisteredService();
        r.setName("verifyOAuthServices");
        r.setServiceId("testId");
        r.setTheme("theme");
        r.setDescription("description");
        r.setAttributeReleasePolicy(new ReturnAllAttributeReleasePolicy());
        r.setClientId("testoauthservice");
        r.setClientSecret("anothertest");
        r.setBypassApprovalPrompt(true);
        final RegisteredService r2 = this.serviceRegistry.save(r);
        assertEquals(r, r2);
    }

    @Test
    public void verifySamlService() {
        final SamlRegisteredService r = new SamlRegisteredService();
        r.setName("verifySamlService");
        r.setServiceId("Testing");
        r.setDescription("description");
        r.setAttributeReleasePolicy(new ReturnAllAttributeReleasePolicy());
        final Map fmt = new HashMap();
        fmt.put("key", "value");
        r.setAttributeNameFormats(fmt);
        r.setMetadataCriteriaDirection("INCLUDE");
        r.setMetadataCriteriaRemoveEmptyEntitiesDescriptors(true);
        r.setMetadataSignatureLocation("location");
        r.setRequiredAuthenticationContextClass("Testing");
        final SamlRegisteredService r2 = (SamlRegisteredService) this.serviceRegistry.save(r);
        assertEquals(r, r2);
    }

    @Test
    public void verifyExpiredServiceDeleted() {
        final RegexRegisteredService r = new RegexRegisteredService();
        r.setServiceId("testExpired");
        r.setName("expired");
        r.setExpirationPolicy(new DefaultRegisteredServiceExpirationPolicy(true, LocalDateTime.now().minusSeconds(1)));
        final RegisteredService r2 = this.servicesManager.save(r);
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis() + 2000);
        this.servicesManager.load();
        final RegisteredService svc = this.servicesManager.findServiceBy(r2.getServiceId());
        assertNull(svc);
    }

    @Test
    public void verifyExpiredServiceDisabled() {
        final RegexRegisteredService r = new RegexRegisteredService();
        r.setServiceId("testExpired1");
        r.setName("expired1");
        final LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(1);
        r.setExpirationPolicy(new DefaultRegisteredServiceExpirationPolicy(false, expirationDate));
        final RegisteredService r2 = this.servicesManager.save(r);
        RegisteredService svc = this.servicesManager.findServiceBy(r2.getServiceId());
        assertNotNull(svc);
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis() + 2000);
        svc = this.servicesManager.findServiceBy(r2.getServiceId());
        assertNotNull(svc);
        assertFalse(svc.getAccessStrategy().isServiceAccessAllowed());
    }

    @TestConfiguration("timeAwareServicesManagerConfiguration")
    public static class TimeAwareServicesManagerConfiguration {

        @Autowired
        @Qualifier("serviceRegistry")
        private ServiceRegistry serviceRegistry;

        @Bean
        public ServicesManager servicesManager() {
            return new TimeAwareServicesManager(serviceRegistry);
        }

        public static class TimeAwareServicesManager extends DefaultServicesManager {
            public TimeAwareServicesManager(final ServiceRegistry serviceRegistry) {
                super(serviceRegistry, null);
            }

            @Override
            protected LocalDateTime getCurrentSystemTime() {
                return org.apereo.cas.util.DateTimeUtils.localDateTimeOf(DateTimeUtils.currentTimeMillis());
            }
        }
    }
}
