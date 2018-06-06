package org.apereo.cas.oidc.jwks;

import org.apereo.cas.oidc.AbstractOidcTests;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

import static org.junit.Assert.*;

/**
 * This is {@link OidcDefaultJsonWebKeystoreCacheLoaderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@TestPropertySource(properties = {
    "cas.authn.oidc.issuer=https://sso.example.org/cas/oidc",
    "cas.authn.oidc.jwksFile=classpath:keystore.jwks"
})
public class OidcDefaultJsonWebKeystoreCacheLoaderTests extends AbstractOidcTests {
    @Test
    public void verifyOperation() {
        assertTrue(oidcDefaultJsonWebKeystoreCache.get("https://sso.example.org/cas/oidc").isPresent());
        assertTrue(oidcDefaultJsonWebKeystoreCache.get("https://sso.example.org/cas/oidc").isPresent());
    }
}
