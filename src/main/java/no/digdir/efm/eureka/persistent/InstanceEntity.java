package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.InstanceInfo;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(
        indexes = {
                @Index(unique = true, columnList = "appName, instanceId"),
        }
)
@Data
public class InstanceEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @NotNull
    private String instanceId;

    @NotNull
    private String appName;

    @Lob
    @Convert(converter = InstanceInfoJsonConverter.class)
    private InstanceInfo instanceInfo;
}
