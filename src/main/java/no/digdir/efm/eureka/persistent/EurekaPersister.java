package no.digdir.efm.eureka.persistent;

import lombok.RequiredArgsConstructor;
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
        instanceEntityService.persist(event.getInstanceInfo());
    }

    @EventListener
    public void onInstanceRenewedEvent(EurekaInstanceRenewedEvent event) {
        instanceEntityService.persist(event.getInstanceInfo());
    }
}
