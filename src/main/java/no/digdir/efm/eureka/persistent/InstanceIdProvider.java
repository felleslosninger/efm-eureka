package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InstanceIdProvider {

    public static final String INTEGRASJONSPUNKT = "INTEGRASJONSPUNKT";
    public static final String ORGNUMBER = "orgnumber";
    public static final String IP_PREFIX = "IP:";

    String getId(InstanceInfo instanceInfo) {
        if (INTEGRASJONSPUNKT.equals(instanceInfo.getAppName())) {
            return Optional.ofNullable(instanceInfo.getMetadata().get(ORGNUMBER))
                    .map(p -> IP_PREFIX + p)
                    .orElseGet(instanceInfo::getInstanceId);
        }
        return instanceInfo.getInstanceId();
    }
}
