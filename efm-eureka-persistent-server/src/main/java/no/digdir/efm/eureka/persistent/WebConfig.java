package no.digdir.efm.eureka.persistent;

import com.netflix.eureka.resources.ServerCodecs;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    private final ServerCodecs serverCodecs;

    public WebConfig(ServerCodecs serverCodecs) {
        this.serverCodecs = serverCodecs;
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new EfmEurekaHttpMessageConverter(serverCodecs));
    }

    @Override
    protected Map<String, MediaType> getDefaultMediaTypes() {
        return Collections.singletonMap("json", MediaType.APPLICATION_JSON);
    }
}