package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.DefaultEurekaServerConfig;
import com.netflix.eureka.resources.DefaultServerCodecs;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({DefaultEurekaServerConfig.class, DefaultServerCodecs.class})
public class InstanceEntityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InstanceEntityRepository target;

    private InstanceEntity instanceIP;
    private InstanceEntity instanceAdminApi;

    @Before
    public void before() {
        instanceIP = entityManager.persist(new InstanceEntity()
                .setAppName(InstanceIdProvider.INTEGRASJONSPUNKT)
                .setInstanceId("IP:987654321")
                .setInstanceInfo(new InstanceInfo("IP:987654321", InstanceIdProvider.INTEGRASJONSPUNKT, "", "127.0.0.1", "", null, null, "", "", "", "", "",
                        "", 0, null, "", null, null, null, null, null, null, null, null, null, null))
        );

        instanceAdminApi = entityManager.persistAndFlush(new InstanceEntity()
                .setAppName("ADMIN_API")
                .setInstanceId("IP:987654321")
                .setInstanceInfo(new InstanceInfo("A:B:C", "ADMIN_API", "", "127.0.0.1", "", null, null, "", "", "", "", "",
                        "", 0, null, "", null, null, null, null, null, null, null, null, null, null))
        );
    }

    @Test
    public void testFindByAppName() {
        assertThat(target.findByAppName(InstanceIdProvider.INTEGRASJONSPUNKT))
                .containsOnly(instanceIP);

        assertThat(target.findByAppName("ADMIN_API"))
                .containsOnly(instanceAdminApi);
    }

    @Test
    public void testSetInstanceInfo() {
        assertThat(target.setInstanceInfo(instanceIP.getAppName(), instanceIP.getInstanceId(),
                new InstanceInfo("IP:987654321", InstanceIdProvider.INTEGRASJONSPUNKT, "My Group", "127.0.0.1", "", null, null, "", "", "", "", "",
                        "", 0, null, "", null, null, null, null, null, null, null, null, null, null))
        ).isOne();

        entityManager.refresh(instanceIP);

        assertThat(instanceIP.getInstanceInfo().getAppGroupName()).isEqualTo("My Group");
    }
}