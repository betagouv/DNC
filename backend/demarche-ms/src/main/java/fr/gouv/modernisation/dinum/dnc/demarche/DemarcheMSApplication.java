package fr.gouv.modernisation.dinum.dnc.demarche;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Classe principale du Microservice Demarche-MS.
 * L'annotation ComponentScan permet de ne pas traiter les Beans Controllers générés par Swagger-generator
 */
@SpringBootApplication
@ComponentScan(
        basePackages =  { "fr.gouv.modernisation.dinum.dnc.demarche.*", "fr.gouv.modernisation.dinum.dnc.common.*" },
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fr.gouv.modernisation.dinum.dnc.demarche.generated.api.server.*")
)
public class DemarcheMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemarcheMSApplication.class, args);
    }


}
