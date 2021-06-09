package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.resources.ServerCodecs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Component
@Converter
@RequiredArgsConstructor
public class InstanceInfoJsonConverter implements AttributeConverter<InstanceInfo, String> {

    private final ServerCodecs serverCodecs;

    @Override
    public String convertToDatabaseColumn(InstanceInfo instanceInfo) {
        if (instanceInfo == null) {
            return null;
        }

        try {
            return serverCodecs.getFullJsonCodec().encode(instanceInfo);
        } catch (IOException e) {
            throw new EfmEurekaException(String.format("Couldn't encode InstanceInfo: %s", instanceInfo), e);
        }
    }

    @Override
    public InstanceInfo convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            return serverCodecs.getFullJsonCodec().decode(dbData, InstanceInfo.class);
        } catch (IOException e) {
            throw new EfmEurekaException(String.format("Couldn't decode InstanceInfo: %s", dbData), e);
        }
    }
}
