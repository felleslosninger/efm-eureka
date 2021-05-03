package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InstanceEntityServiceTest {

    private static final String INSTANCE_ID = "instanceId";
    private static final String APP_NAME = "AppName";

    @Mock private InstanceIdProvider instanceIdProvider;
    @Mock private InstanceEntityRepository repository;

    @InjectMocks private InstanceEntityService target;

    @Mock private InstanceInfo instanceInfo;
    @Mock private List<InstanceEntity> instanceEntityList;

    @Captor private ArgumentCaptor<InstanceEntity> instanceEntityArgumentCaptor;

    @After
    public void after() {
        Mockito.verifyNoMoreInteractions(instanceIdProvider, repository);
    }

    @Test
    public void testPersistCreate() {
        given(instanceIdProvider.getId(any())).willReturn(INSTANCE_ID);
        given(repository.setInstanceInfo(any(), any(), any())).willReturn(0);

        given(instanceInfo.getAppName()).willReturn(APP_NAME);

        target.persist(instanceInfo);

        verify(instanceIdProvider).getId(instanceInfo);
        verify(repository).setInstanceInfo(APP_NAME, INSTANCE_ID, instanceInfo);

        verify(repository).save(instanceEntityArgumentCaptor.capture());

        assertThat(instanceEntityArgumentCaptor.getValue())
                .satisfies(p -> {
                    assertThat(p.getInstanceId()).isEqualTo(INSTANCE_ID);
                    assertThat(p.getAppName()).isEqualTo(APP_NAME);
                    assertThat(p.getInstanceInfo()).isSameAs(instanceInfo);
                });
    }

    @Test
    public void testPersistUpdate() {
        given(instanceIdProvider.getId(any())).willReturn(INSTANCE_ID);
        given(repository.setInstanceInfo(any(), any(), any())).willReturn(1);

        given(instanceInfo.getAppName()).willReturn(APP_NAME);

        target.persist(instanceInfo);

        verify(instanceIdProvider).getId(instanceInfo);
        verify(repository).setInstanceInfo(APP_NAME, INSTANCE_ID, instanceInfo);
    }

    @Test
    public void testFindByAppName() {
        given(repository.findByAppName(any())).willReturn(instanceEntityList);
        assertThat(target.findByAppName(APP_NAME)).isSameAs(instanceEntityList);
        verify(repository).findByAppName(APP_NAME);
    }
}