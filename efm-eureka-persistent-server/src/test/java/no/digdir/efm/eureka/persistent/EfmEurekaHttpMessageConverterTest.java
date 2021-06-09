package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrapper;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.resources.ServerCodecs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EfmEurekaHttpMessageConverterTest.Config.class})
public class EfmEurekaHttpMessageConverterTest {


    @Configuration
    @Import(EfmEurekaHttpMessageConverter.class)
    public static class Config {

        @Primary
        @Bean
        public HttpServletRequest httpServletRequest() {
            return mock(HttpServletRequest.class);
        }
    }

    @MockBean private ServerCodecs serverCodecs;
    @Autowired private EfmEurekaHttpMessageConverter converter;

    @MockBean private CodecWrapper codeWrapper;
    @MockBean private Application application;
    @MockBean private InputStream inputStream;

    @Before
    public void before() {
        reset(serverCodecs);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(serverCodecs);
    }

    @Test
    public void canRead() {
        assertThat(converter.canRead(InstanceInfo.class, MediaType.APPLICATION_JSON)).isTrue();
        assertThat(converter.canRead(InstanceInfo.class, MediaType.parseMediaType("application/vnd.spring-boot.actuator.v3+json"))).isTrue();
    }

    @Test
    public void canWrite() {
        assertThat(converter.canWrite(InstanceInfo.class, MediaType.APPLICATION_JSON)).isTrue();
        assertThat(converter.canWrite(InstanceInfo.class, MediaType.parseMediaType("application/vnd.spring-boot.actuator.v3+json"))).isTrue();
    }

    @Test
    public void getSupportedMediaTypes() {
        assertThat(converter.getSupportedMediaTypes())
                .containsExactly(MediaType.APPLICATION_JSON, MediaType.ALL);
    }

    @Test
    public void readJson() throws IOException {
        given(serverCodecs.getFullJsonCodec()).willReturn(codeWrapper);
        given(codeWrapper.decode(any(InputStream.class), any())).willReturn(application);

        assertThat(converter.read(Application.class, new MockHttpInputMessage(inputStream)))
                .isInstanceOf(Application.class)
                .isSameAs(application);

        verify(serverCodecs).getFullJsonCodec();
        verify(codeWrapper).decode(inputStream, Application.class);
    }

    @Test
    public void writeJSON() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();

        given(serverCodecs.getFullJsonCodec()).willReturn(codeWrapper);

        converter.write(application, MediaType.APPLICATION_JSON, outputMessage);

        verify(serverCodecs).getFullJsonCodec();
        verify(codeWrapper).encode(same(application), same(outputMessage.getBody()));
    }
}