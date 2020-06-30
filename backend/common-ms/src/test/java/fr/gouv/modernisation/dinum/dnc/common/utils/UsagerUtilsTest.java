package fr.gouv.modernisation.dinum.dnc.common.utils;

import com.github.javafaker.Faker;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.Adresse;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires sur {@link UsagerUtils}
 */
public class UsagerUtilsTest {

	/**
	 * Logger {@link Log}
	 */
	private static final Log LOGGER = LogFactory.getLog(UsagerUtilsTest.class);

	private final Faker faker = new Faker(new Locale("fr-FR"));

	@Test
	public void nullValueTest(){
		assertNull(UsagerUtils.getUsagerIdFromIdentitePivot(null));
	}

	@Test
	public void idUsagerAvecChampsSignificatifsManquantsTest(){
		IdentitePivot identitePivot = new IdentitePivot();
		identitePivot.setFamilyName(faker.name().lastName());
		identitePivot.setGender("male");
		assertNotNull(UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot));
	}

	@RepeatedTest(5)
	public void idUsagerSameObjectTest(){
		IdentitePivot identitePivot = getRandomIdentitePivot();


		String idUsager1 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);
		String idUsager2 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);

		assertEquals(idUsager1,idUsager2);
	}

	@RepeatedTest(5)
	public void idUsagerValeursNonSignificativesNonInfluentesTest(){
		IdentitePivot identitePivot = getRandomIdentitePivot();
		//Création de l'idUsager d'origine
		String idUsager1 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);
		//Modification de données non significatives
		identitePivot.preferredUsername(faker.name().username())
				.phoneNumber(faker.phoneNumber().phoneNumber())
				.email(faker.name().username()+"@test.com")
				.getAddress()
						.streetAddress(faker.address().streetAddress())
						.country(faker.address().country())
						.locality(faker.address().cityName())
						.postalCode(faker.address().zipCode())
				;

		//Recalcule de l'idUsager
		String idUsager2 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);

		assertEquals(idUsager1,idUsager2);
	}

	@RepeatedTest(5)
	public void idUsagerValeursSignificativesInfluentesGivenNameTest(){
		IdentitePivot identitePivot = getRandomIdentitePivot();
		//Création de l'idUsager d'origine
		String idUsager1 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);
		//Modification de données non significatives
		String oldValue = identitePivot.getGivenName();
		identitePivot.givenName(faker.name().firstName());
		LOGGER.info(String.format("Old value : %s, New value : %s", oldValue, identitePivot.getGivenName()));

		//Recalcule de l'idUsager
		String idUsager2 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);

		assertNotEquals(idUsager1,idUsager2);
	}

	@RepeatedTest(5)
	public void idUsagerValeursSignificativesInfluentesFamilyNameTest(){
		IdentitePivot identitePivot = getRandomIdentitePivot();
		//Création de l'idUsager d'origine
		String idUsager1 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);
		//Modification de données non significatives
		String oldValue = identitePivot.getFamilyName();
		identitePivot.familyName(faker.name().lastName());
		LOGGER.info(String.format("Old value : %s, New value : %s", oldValue, identitePivot.getFamilyName()));

		//Recalcule de l'idUsager
		String idUsager2 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);


		assertNotEquals(idUsager1,idUsager2);
	}

	@RepeatedTest(5)
	public void idUsagerValeursSignificativesInfluentesGenderTest(){
		IdentitePivot identitePivot = getRandomIdentitePivot();
		//Création de l'idUsager d'origine
		String idUsager1 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);
		//Modification de données non significatives
		identitePivot.gender(StringUtils.equals("male", identitePivot.getGender()) ? "female" : "male");

		//Recalcule de l'idUsager
		String idUsager2 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);

		assertNotEquals(idUsager1,idUsager2);
	}

	@RepeatedTest(5)
	public void idUsagerValeursSignificativesInfluentesBirthdateTest(){
		IdentitePivot identitePivot = getRandomIdentitePivot();
		//Création de l'idUsager d'origine
		String idUsager1 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);
		//Modification de données non significatives
		identitePivot.birthdate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

		//Recalcule de l'idUsager
		String idUsager2 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);

		assertNotEquals(idUsager1,idUsager2);
	}

	@RepeatedTest(5)
	public void idUsagerValeursSignificativesInfluentesBirthCountryTest(){
		IdentitePivot identitePivot = getRandomIdentitePivot();
		//Création de l'idUsager d'origine
		String idUsager1 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);
		//Modification de données non significatives
		identitePivot.birthcountry(faker.address().country());

		//Recalcule de l'idUsager
		String idUsager2 = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);

		assertNotEquals(idUsager1,idUsager2);
	}

	private IdentitePivot getRandomIdentitePivot() {
		return new IdentitePivot()
				.givenName(faker.name().firstName())
				.familyName(faker.name().lastName())
				.birthdate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
				.gender(faker.bool().bool() ? "male" : "female")
				.birthplace(faker.address().cityName())
				.birthcountry(faker.address().country())
				.preferredUsername(faker.name().username())
				.phoneNumber(faker.phoneNumber().phoneNumber())
				.email(faker.name().username()+"@test.com")
				.address(new Adresse()
						.streetAddress(faker.address().streetAddress())
						.country(faker.address().country())
						.locality(faker.address().cityName())
						.postalCode(faker.address().zipCode())
				);
	}
}
