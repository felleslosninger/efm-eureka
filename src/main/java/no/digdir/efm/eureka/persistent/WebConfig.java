package no.digdir.efm.eureka.persistent;

import com.netflix.discovery.converters.JsonXStream;
import com.netflix.discovery.converters.XmlXStream;
import com.netflix.eureka.resources.ServerCodecs;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    private final ServerCodecs serverCodecs;
    private final HttpServletRequest request;

    public WebConfig(ServerCodecs serverCodecs, HttpServletRequest request) {
        this.serverCodecs = serverCodecs;
        this.request = request;
        JsonXStream.getInstance().registerConverter(new DateConverter(), XStream.PRIORITY_VERY_HIGH);
        XmlXStream.getInstance().registerConverter(new DateConverter(), XStream.PRIORITY_VERY_HIGH);
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new EfmEurekaHttpMessageConverter(request, serverCodecs));
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/eureka")
                .setCachePeriod(3000);
    }
}