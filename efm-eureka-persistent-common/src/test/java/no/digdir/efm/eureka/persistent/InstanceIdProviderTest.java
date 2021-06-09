package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class InstanceIdProviderTest {

    @InjectMocks private InstanceIdProvider target;

    @Mock private InstanceInfo instanceInfo;

    @Test
    public void testGetId() {
        given(instanceInfo.getAppName()).willReturn("AppName");
        given(instanceInfo.getInstanceId()).willReturn("instanceId");
        assertThat(target.getId(instanceInfo)).isEqualTo("instanceId");
    }

    @Test
    public void testGetIdForAppNameINTEGRASJONSPUNKT() {
        given(instanceInfo.getAppName()).willReturn(InstanceIdProvider.INTEGRASJONSPUNKT);
        given(instanceInfo.getMetadata()).willReturn(Collections.singletonMap(InstanceIdProvider.ORGNUMBER, "987654321"));
        assertThat(target.getId(instanceInfo)).isEqualTo("IP:987654321");
    }

    @Test
    public void testGetIdForAppNameINTEGRASJONSPUNKTWhenMissingMetadataOrgnummer() {
        given(instanceInfo.getAppName()).willReturn(InstanceIdProvider.INTEGRASJONSPUNKT);
        given(instanceInfo.getInstanceId()).willReturn("instanceId");
        given(instanceInfo.getMetadata()).willReturn(Collections.emptyMap());
        assertThat(target.getId(instanceInfo)).isEqualTo("instanceId");
    }
}