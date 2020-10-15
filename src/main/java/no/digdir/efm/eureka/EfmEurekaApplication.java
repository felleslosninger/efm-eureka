package no.digdir.efm.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableConfigServer
@EnableEurekaServer
@EnableCaching
public class EfmEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EfmEurekaApplication.class, args);
    }

}
