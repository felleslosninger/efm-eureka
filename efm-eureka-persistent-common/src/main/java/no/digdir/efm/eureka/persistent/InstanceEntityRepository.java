package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstanceEntityRepository extends JpaRepository<InstanceEntity, String> {

    List<InstanceEntity> findByAppName(String appName);

    @Modifying
    @Query("update InstanceEntity set instanceInfo = :instanceInfo\n" +
            "where appName = :appName and instanceId = :instanceId")
    int setInstanceInfo(String appName, String instanceId, InstanceInfo instanceInfo);
}
