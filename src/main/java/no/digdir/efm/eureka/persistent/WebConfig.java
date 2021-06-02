package no.digdir.efm.eureka.persistent;

import com.netflix.discovery.converters.JsonXStream;
import com.netflix.discovery.converters.XmlXStream;
import com.netflix.eureka.resources.ServerCodecs;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
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
        JsonXStream jsonXStream = JsonXStream.getInstance();
        XmlXStream xmlXStream = XmlXStream.getInstance();
        jsonXStream.registerConverter(new DateConverter(), XStream.PRIORITY_VERY_HIGH);
        xmlXStream.registerConverter(new DateConverter(), XStream.PRIORITY_VERY_HIGH);
        jsonXStream.registerConverter(new WebConfig.PropertySourceDescriptorConverter(jsonXStream.getMapper()), XStream.PRIORITY_VERY_HIGH);
        xmlXStream.registerConverter(new WebConfig.PropertySourceDescriptorConverter(xmlXStream.getMapper()), XStream.PRIORITY_VERY_HIGH);
        jsonXStream.alias("propertySource", EnvironmentEndpoint.PropertySourceDescriptor.class);
        xmlXStream.alias("propertySource", EnvironmentEndpoint.PropertySourceDescriptor.class);
        jsonXStream.alias("environment", EnvironmentEndpoint.EnvironmentDescriptor.class);
        xmlXStream.alias("environment", EnvironmentEndpoint.EnvironmentDescriptor.class);
        jsonXStream.alias("propertyValue", EnvironmentEndpoint.PropertyValueDescriptor.class);
        xmlXStream.alias("propertyValue", EnvironmentEndpoint.PropertyValueDescriptor.class);
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

    public static class PropertySourceDescriptorConverter extends AbstractCollectionConverter {

        public PropertySourceDescriptorConverter(Mapper mapper) {
            super(mapper);
        }

        @Override
        public boolean canConvert(Class type) {
            return EnvironmentEndpoint.PropertySourceDescriptor.class == type;
        }

        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            EnvironmentEndpoint.PropertySourceDescriptor descriptor = (EnvironmentEndpoint.PropertySourceDescriptor) source;

            writer.addAttribute("name", descriptor.getName());
            HierarchicalStreamWriter underlyingWriter = writer.underlyingWriter();
            descriptor.getProperties().forEach((key, value) -> {
                underlyingWriter.startNode("property");
                writer.addAttribute("name", key);
                writeCompleteItem(value, context, underlyingWriter);
                underlyingWriter.endNode();
            });
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null;
        }
    }
}