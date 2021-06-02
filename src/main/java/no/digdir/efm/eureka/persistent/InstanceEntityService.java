package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstanceEntityService {

    private final InstanceIdProvider instanceIdProvider;
    private final InstanceEntityRepository repository;

    @Transactional
    public void persist(InstanceInfo instanceInfo) {
        String instanceId = instanceIdProvider.getId(instanceInfo);

        if (repository.setInstanceInfo(instanceInfo.getAppName(), instanceId, instanceInfo) == 0) {
            repository.save(new InstanceEntity()
                    .setInstanceId(instanceId)
                    .setAppName(instanceInfo.getAppName())
                    .setInstanceInfo(instanceInfo));
        }
    }

    @Transactional(readOnly = true)
    public List<InstanceEntity> findByAppName(String appName) {
        return repository.findByAppName(appName);
    }
}
