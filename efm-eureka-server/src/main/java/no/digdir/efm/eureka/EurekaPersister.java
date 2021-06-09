package no.digdir.efm.eureka;

import lombok.RequiredArgsConstructor;
import no.digdir.efm.eureka.persistent.InstanceEntityService;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EurekaPersister {

    private final InstanceEntityService instanceEntityService;

    @EventListener
    public void onInstanceRegisteredEvent(EurekaInstanceRegisteredEvent event) {
        if (event.getInstanceInfo() != null) {
            instanceEntityService.persist(event.getInstanceInfo());
        }
    }

    @EventListener
    public void onInstanceRenewedEvent(EurekaInstanceRenewedEvent event) {
        if (event.getInstanceInfo() != null) {
            instanceEntityService.persist(event.getInstanceInfo());
        }
    }
}
