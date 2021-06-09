package no.digdir.efm.eureka.persistent;

import com.netflix.discovery.shared.Application;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/persistent/eureka")
public class InstantController {

    private final InstanceEntityService instanceEntityService;

    @GetMapping("apps/{appName}")
    public Application getApplication(@PathVariable String appName) {
        String upperCasedAppName = appName.toUpperCase(Locale.ROOT);
        return new Application(
                upperCasedAppName,
                instanceEntityService.findByAppName(upperCasedAppName)
                        .stream()
                        .map(InstanceEntity::getInstanceInfo)
                        .collect(Collectors.toList())
        );
    }
}
