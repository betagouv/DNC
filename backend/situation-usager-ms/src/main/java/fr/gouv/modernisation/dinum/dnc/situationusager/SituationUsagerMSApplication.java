package fr.gouv.modernisation.dinum.dnc.situationusager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
		basePackages =  { "fr.gouv.modernisation.dinum.dnc.situationusager.*", "fr.gouv.modernisation.dinum.dnc.common.*" },
		excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.*")
)
public class SituationUsagerMSApplication {

	public static void main(String[] args) {
		SpringApplication.run(SituationUsagerMSApplication.class, args);
	}

}

