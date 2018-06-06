package org.apereo.cas.adaptors.u2f.storage;

import com.yubico.u2f.data.DeviceRegistration;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.util.crypto.CertUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.security.cert.X509Certificate;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * This is {@link AbstractU2FDeviceRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public abstract class AbstractU2FDeviceRepositoryTests {

    @Test
    public void verifyDeviceSaved() {
        try {
            final X509Certificate cert = CertUtils.readCertificate(new ClassPathResource("cert.crt"));
            final DeviceRegistration r1 = new DeviceRegistration("keyhandle11", "publickey1", cert, 1);
            final DeviceRegistration r2 = new DeviceRegistration("keyhandle22", "publickey1", cert, 2);
            getDeviceRepository().registerDevice("casuser", r1);
            getDeviceRepository().registerDevice("casuser", r2);
            final Collection<DeviceRegistration> devs = getDeviceRepository().getRegisteredDevices("casuser");
            assertEquals(2, devs.size());
        } catch (final Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    protected abstract U2FDeviceRepository getDeviceRepository();
}
