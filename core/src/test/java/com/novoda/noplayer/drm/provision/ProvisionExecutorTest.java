package com.novoda.noplayer.drm.provision;

import com.novoda.noplayer.drm.ModularDrmProvisionRequest;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProvisionExecutorTest {

    private static final String PROVISION_URL = "http://provisionurl.com";
    private static final byte[] PROVISION_DATA = "provision-payload".getBytes();
    private static final ModularDrmProvisionRequest A_PROVISION_REQUEST = new ModularDrmProvisionRequest(PROVISION_URL, PROVISION_DATA);

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private ProvisionExecutor provisionExecutor;
    private ArgumentCaptor<String> provisionUrlCaptor;

    @Mock
    private HttpUrlConnectionPoster httpUrlConnectionPoster;
    @Mock
    private ProvisioningCapabilities capabilities;

    @Before
    public void setUp() {
        provisionUrlCaptor = ArgumentCaptor.forClass(String.class);
        provisionExecutor = new ProvisionExecutor(httpUrlConnectionPoster, capabilities);
    }

    @Test(expected = UnableToProvisionException.class)
    public void givenNonCapableProvisionCapabilities_whenProvisioning_thenAnUnableToProvisionExceptionIsThrown() throws IOException, UnableToProvisionException {
        when(capabilities.canProvision()).thenReturn(false);

        provisionExecutor.execute(A_PROVISION_REQUEST);
    }

    @Test
    public void givenCapableProvisionCapabilities_whenProvisioning_thenTheRequestUrlIsExpected() throws IOException, UnableToProvisionException {
        when(capabilities.canProvision()).thenReturn(true);
        String expectedProvisionUrl = PROVISION_URL + "&signedRequest=" + new String(PROVISION_DATA);

        provisionExecutor.execute(A_PROVISION_REQUEST);
        verify(httpUrlConnectionPoster).post(provisionUrlCaptor.capture());

        assertThat(provisionUrlCaptor.getValue()).isEqualTo(expectedProvisionUrl);
    }
}
