package fr.gouv.modernisation.dinum.dnc.franceconnect;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
		basePackages =  { "fr.gouv.modernisation.dinum.dnc.franceconnect.*", "fr.gouv.modernisation.dinum.dnc.common.*" },
		excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.server.*")
)
public class FranceConnectMS {

	public static void main(String[] args) {
		SpringApplication.run(FranceConnectMS.class, args);
	}
}