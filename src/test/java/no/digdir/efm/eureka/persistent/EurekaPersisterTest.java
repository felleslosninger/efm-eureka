package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationContextRunner.class)
public class EurekaPersisterTest {

    @Autowired private ApplicationContextRunner runner;
    @MockBean private InstanceEntityService instanceEntityService;
    @Mock private InstanceInfo instanceInfo;

    @Test
    public void testOnInstanceRegisteredEvent() {
        runner
                .withBean(EurekaPersister.class, instanceEntityService)
                .run(
                        context -> {
                            context.publishEvent(new EurekaInstanceRegisteredEvent(this, instanceInfo, 3000, false));
                            verify(instanceEntityService).persist(instanceInfo);
                        });
    }

    @Test
    public void testOnInstanceRenewedEvent() {
        runner
                .withBean(EurekaPersister.class, instanceEntityService)
                .run(
                        context -> {
                            context.publishEvent(new EurekaInstanceRenewedEvent(this, "AppName", "ServerId", instanceInfo, false));
                            verify(instanceEntityService).persist(instanceInfo);
                        });
    }
}