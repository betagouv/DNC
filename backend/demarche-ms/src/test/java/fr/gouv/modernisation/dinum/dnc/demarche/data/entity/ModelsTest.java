package fr.gouv.modernisation.dinum.dnc.demarche.data.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanTester;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests des modèles du projets
 */
public class ModelsTest {

	private BeanTester tester;

	@BeforeEach
	public void init() {
		tester = new BeanTester();
		tester.setIterations(1);
		tester.getFactoryCollection().addFactory(LocalDate.class, LocalDate::now);
		tester.getFactoryCollection().addFactory(LocalDateTime.class, LocalDateTime::now);
	}

	@Test
	@DisplayName("Tests des Getter/Setter de DemarcheDO")
	public void testDemarcheDo() {
		tester.testBean(Demarche.class);
	}
}
