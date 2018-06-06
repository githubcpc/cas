package org.apereo.cas.support.saml.util;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.SamlIdPTestUtils;
import org.apereo.cas.support.saml.SamlIdPUtils;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.util.CollectionUtils;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.BindingCriterion;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;

import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link SamlIdPUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class SamlIdPUtilsTests extends BaseSamlIdPConfigurationTests {
    @Test
    public void verifyMetadataForAllServices() throws Exception {
        final SamlRegisteredService service = SamlIdPTestUtils.getSamlRegisteredService();
        servicesManager.save(service);
        final MetadataResolver md = SamlIdPUtils.getMetadataResolverForAllSamlServices(servicesManager, service.getServiceId(),
            samlRegisteredServiceCachingMetadataResolver);
        assertNotNull(md);

        final CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new EntityIdCriterion(service.getServiceId()));
        criteriaSet.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        criteriaSet.add(new BindingCriterion(CollectionUtils.wrap(SAMLConstants.SAML2_POST_BINDING_URI)));
        final Iterator<EntityDescriptor> it = md.resolve(criteriaSet).iterator();
        assertTrue(it.hasNext());
        assertEquals(service.getServiceId(), it.next().getEntityID());
    }

    @Test
    public void verifyAssertionConsumerServiceNoIndex() throws Exception {
        final SamlRegisteredService service = SamlIdPTestUtils.getSamlRegisteredService();
        servicesManager.save(service);

        final AuthnRequest authnRequest = mock(AuthnRequest.class);
        when(authnRequest.getProtocolBinding()).thenReturn(SAMLConstants.SAML2_POST_BINDING_URI);
        when(authnRequest.getAssertionConsumerServiceIndex()).thenReturn(null);
        when(authnRequest.getAssertionConsumerServiceURL()).thenReturn("https://sp.testshib.org/Shibboleth.sso/SAML/POST");
        final AssertionConsumerService acs = SamlIdPUtils.getAssertionConsumerServiceFor(authnRequest, servicesManager,
            samlRegisteredServiceCachingMetadataResolver);
        assertNotNull(acs);
    }

    @Test
    public void verifyAssertionConsumerServiceWithIndex() throws Exception {
        final SamlRegisteredService service = SamlIdPTestUtils.getSamlRegisteredService();
        servicesManager.save(service);

        final AuthnRequest authnRequest = mock(AuthnRequest.class);
        final Issuer issuer = mock(Issuer.class);
        when(issuer.getValue()).thenReturn(service.getServiceId());
        when(authnRequest.getIssuer()).thenReturn(issuer);
        when(authnRequest.getProtocolBinding()).thenReturn(SAMLConstants.SAML2_POST_BINDING_URI);
        when(authnRequest.getAssertionConsumerServiceIndex()).thenReturn(0);
        final AssertionConsumerService acs = SamlIdPUtils.getAssertionConsumerServiceFor(authnRequest, servicesManager,
            samlRegisteredServiceCachingMetadataResolver);
        assertNotNull(acs);
    }
}
