package fr.gouv.modernisation.dinum.dnc.partenaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Classe principale du Microservice Partenaire-MS.
 * L'annotation ComponentScan permet de ne pas traiter les Beans Controllers générés par Swagger-generator
 */
@SpringBootApplication
@ComponentScan(
        basePackages =  { "fr.gouv.modernisation.dinum.dnc.partenaire.*" },
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.server.*")
)
public class PartenaireMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartenaireMSApplication.class, args);
    }


}
