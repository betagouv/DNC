package fr.gouv.modernisation.dinum.dnc.franceconnect.model;

import fr.gouv.modernisation.dinum.dnc.franceconnect.factory.IdentitePivotFactory;
import fr.gouv.modernisation.dinum.dnc.franceconnect.factory.SessionFactory;
import fr.gouv.modernisation.dinum.dnc.franceconnect.factory.TokenFranceconnectFactory;
import fr.gouv.modernisation.dinum.dnc.franceconnect.factory.UUIDFactory;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenFranceconnect;
import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.data.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.meanbean.test.BeanTester;

import java.util.UUID;

/**
 * Classe permettant de tester les models du projets
 */
public class ModelTest {

	private BeanTester tester;

	@BeforeEach
	public void init() {
		tester = new BeanTester();
		tester.setIterations(1);

		tester.getFactoryCollection().addFactory(TokenFranceconnect.class, new TokenFranceconnectFactory());
		tester.getFactoryCollection().addFactory(IdentitePivot.class, new IdentitePivotFactory());
		tester.getFactoryCollection().addFactory(Session.class, new SessionFactory());
		tester.getFactoryCollection().addFactory(UUID.class, new UUIDFactory());
	}

	@Test
	@DisplayName("Test Getter/Setter Session")
	public void sessionTest(){
		tester.testBean(Session.class);
	}

}
