package no.digdir.efm.eureka.persistent;

import com.netflix.discovery.converters.wrappers.CodecWrappers;
import com.netflix.eureka.resources.DefaultServerCodecs;
import com.netflix.eureka.resources.ServerCodecs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.netflix.discovery.converters.wrappers.CodecWrappers.getCodecName;

@Configuration
public class EurekaConfig {

    @Bean
    public ServerCodecs serverCodecs() {
        return new EurekaConfig.CloudServerCodecs();
    }

    static class CloudServerCodecs extends DefaultServerCodecs {

        CloudServerCodecs() {
            super(CodecWrappers.getCodec(CodecWrappers.LegacyJacksonJson.class),
                    CodecWrappers.getCodec(CodecWrappers.JacksonJsonMini.class),
                    CodecWrappers.getCodec(CodecWrappers.XStreamXml.class),
                    CodecWrappers.getCodec(CodecWrappers.JacksonXmlMini.class));
        }
    }
}
