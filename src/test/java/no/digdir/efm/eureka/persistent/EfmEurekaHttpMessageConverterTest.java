package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.EurekaAccept;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrapper;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.registry.Key;
import com.netflix.eureka.resources.ServerCodecs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
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

    @Autowired private HttpServletRequest request;
    @MockBean private ServerCodecs serverCodecs;
    @Autowired private EfmEurekaHttpMessageConverter converter;

    @MockBean private CodecWrapper codeWrapper;
    @MockBean private Application application;
    @MockBean private InputStream inputStream;

    @Before
    public void before() {
        Mockito.reset(request, serverCodecs);
    }

    @After
    public void after() {
        Mockito.verifyNoMoreInteractions(request, serverCodecs);
    }

    @Test
    public void canRead() {
        assertThat(converter.canRead(InstanceInfo.class, MediaType.APPLICATION_JSON)).isTrue();
        assertThat(converter.canRead(InstanceInfo.class, MediaType.APPLICATION_XML)).isTrue();
    }

    @Test
    public void canWrite() {
        assertThat(converter.canWrite(InstanceInfo.class, MediaType.APPLICATION_JSON)).isTrue();
        assertThat(converter.canWrite(InstanceInfo.class, MediaType.APPLICATION_XML)).isTrue();
    }

    @Test
    public void getSupportedMediaTypes() {
        assertThat(converter.getSupportedMediaTypes())
                .containsExactly(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.ALL);
    }

    @Test
    public void readJson() throws IOException {
        given(request.getHeader(HttpHeaders.ACCEPT)).willReturn(MediaType.APPLICATION_JSON_VALUE);
        given(serverCodecs.getFullJsonCodec()).willReturn(codeWrapper);
        given(codeWrapper.decode(any(InputStream.class), any())).willReturn(application);

        assertThat(converter.read(Application.class, new MockHttpInputMessage(inputStream)))
                .isInstanceOf(Application.class)
                .isSameAs(application);

        verify(request).getHeader(HttpHeaders.ACCEPT);
        verify(serverCodecs).getFullJsonCodec();
        verify(codeWrapper).decode(inputStream, Application.class);
    }

    @Test
    public void readXML() throws IOException {
        given(request.getHeader(HttpHeaders.ACCEPT)).willReturn(MediaType.APPLICATION_XML_VALUE);
        given(serverCodecs.getFullXmlCodec()).willReturn(codeWrapper);
        given(codeWrapper.decode(any(InputStream.class), any())).willReturn(application);

        assertThat(converter.read(Application.class, new MockHttpInputMessage(inputStream)))
                .isInstanceOf(Application.class)
                .isSameAs(application);

        verify(request).getHeader(HttpHeaders.ACCEPT);
        verify(serverCodecs).getFullXmlCodec();
        verify(codeWrapper).decode(inputStream, Application.class);
    }

    @Test
    public void writeJSON() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();

        given(request.getHeader(HttpHeaders.ACCEPT)).willReturn(MediaType.APPLICATION_JSON_VALUE);
        given(request.getHeader(EurekaAccept.HTTP_X_EUREKA_ACCEPT)).willReturn(EurekaAccept.full.name());
        given(serverCodecs.getEncoder(any(), any())).willReturn(codeWrapper);

        converter.write(application, MediaType.APPLICATION_JSON, outputMessage);

        verify(request).getHeader(HttpHeaders.ACCEPT);
        verify(request).getHeader(EurekaAccept.HTTP_X_EUREKA_ACCEPT);
        verify(serverCodecs).getEncoder(Key.KeyType.JSON, EurekaAccept.full);
        verify(codeWrapper).encode(same(application), same(outputMessage.getBody()));
    }

    @Test
    public void writeXML() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();

        given(request.getHeader(HttpHeaders.ACCEPT)).willReturn(MediaType.APPLICATION_XML_VALUE);
        given(request.getHeader(EurekaAccept.HTTP_X_EUREKA_ACCEPT)).willReturn(EurekaAccept.full.name());
        given(serverCodecs.getEncoder(any(), any())).willReturn(codeWrapper);

        converter.write(application, MediaType.APPLICATION_XML, outputMessage);

        verify(request).getHeader(HttpHeaders.ACCEPT);
        verify(request).getHeader(EurekaAccept.HTTP_X_EUREKA_ACCEPT);
        verify(serverCodecs).getEncoder(Key.KeyType.XML, EurekaAccept.full);
        verify(codeWrapper).encode(same(application), same(outputMessage.getBody()));
    }
}