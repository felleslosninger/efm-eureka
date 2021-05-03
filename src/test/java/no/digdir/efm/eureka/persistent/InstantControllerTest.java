package no.digdir.efm.eureka.persistent;

import com.netflix.discovery.shared.Application;
import com.netflix.eureka.DefaultEurekaServerConfig;
import com.netflix.eureka.resources.DefaultServerCodecs;
import com.netflix.eureka.resources.ServerCodecs;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.SneakyThrows;
import no.digdir.efm.eureka.persistent.InstanceEntityService;
import no.digdir.efm.eureka.persistent.InstanceIdProvider;
import no.digdir.efm.eureka.persistent.InstantController;
import no.digdir.efm.eureka.persistent.WebConfig;
import no.digdir.efm.eureka.persistent.InstanceEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = InstantController.class)
@EnableSpringDataWebSupport
@WebAppConfiguration
@Import({DefaultEurekaServerConfig.class, DefaultServerCodecs.class, WebConfig.class})
public class InstantControllerTest {

    @Autowired private ServerCodecs serverCodecs;
    @Autowired private WebApplicationContext webApplicationContext;

    @MockBean
    private InstanceEntityService instanceEntityService;

    protected MockMvc mockMvc;

    @Value("classpath:/application/integrasjonspunkt.json")
    private Resource applicationJsonResource;

    @Value("classpath:/application/integrasjonspunkt.xml")
    private Resource applicationXmlResource;

    private String getResourceAsText(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    @Before
    @SneakyThrows
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @After
    public void after() {
        Mockito.verifyNoMoreInteractions(instanceEntityService);
    }

    @Test
    public void testFindByAppNameJSON() throws Exception {
        String applicationJson = getResourceAsText(applicationJsonResource);
        Application application = serverCodecs.getFullJsonCodec().decode(applicationJson, Application.class);

        given(instanceEntityService.findByAppName(any()))
                .willReturn(application.getInstances()
                        .stream()
                        .map(p -> new InstanceEntity().setInstanceInfo(p))
                        .collect(Collectors.toList())
                );

        mockMvc.perform(MockMvcRequestBuilders.get("/persistent/eureka/apps/{appName}", InstanceIdProvider.INTEGRASJONSPUNKT)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(applicationJson));

        verify(instanceEntityService).findByAppName(InstanceIdProvider.INTEGRASJONSPUNKT);
    }

    @Test
    public void testFindByAppNameXML() throws Exception {
        String applicationXml = getResourceAsText(applicationXmlResource);
        Application application = serverCodecs.getFullXmlCodec().decode(applicationXml, Application.class);

        given(instanceEntityService.findByAppName(any()))
                .willReturn(application.getInstances()
                        .stream()
                        .map(p -> new InstanceEntity().setInstanceInfo(p))
                        .collect(Collectors.toList())
                );

        mockMvc.perform(MockMvcRequestBuilders.get("/persistent/eureka/apps/{appName}", InstanceIdProvider.INTEGRASJONSPUNKT)
                .accept(MediaType.APPLICATION_XML))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(applicationXml));

        verify(instanceEntityService).findByAppName(InstanceIdProvider.INTEGRASJONSPUNKT);
    }

    @Test
    public void testFindByAppNameNoAcceptHeader() throws Exception {
        String applicationXml = getResourceAsText(applicationXmlResource);
        Application application = serverCodecs.getFullXmlCodec().decode(applicationXml, Application.class);

        given(instanceEntityService.findByAppName(any()))
                .willReturn(application.getInstances()
                        .stream()
                        .map(p -> new InstanceEntity().setInstanceInfo(p))
                        .collect(Collectors.toList())
                );

        mockMvc.perform(MockMvcRequestBuilders.get("/persistent/eureka/apps/{appName}", InstanceIdProvider.INTEGRASJONSPUNKT))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(applicationXml));

        verify(instanceEntityService).findByAppName(InstanceIdProvider.INTEGRASJONSPUNKT);
    }
}