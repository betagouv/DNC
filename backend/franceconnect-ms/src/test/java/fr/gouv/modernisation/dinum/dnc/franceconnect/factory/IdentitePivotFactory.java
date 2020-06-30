package fr.gouv.modernisation.dinum.dnc.franceconnect.factory;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import org.meanbean.lang.Factory;

import java.time.LocalDate;
import java.util.UUID;

public class IdentitePivotFactory implements Factory<IdentitePivot> {

	@Override
	public IdentitePivot create() {
		IdentitePivot identitePivot = new IdentitePivot();
		identitePivot.setFamilyName("TEST");
		identitePivot.setGivenName("Test");
		identitePivot.setGender("female");
		identitePivot.setBirthdate(LocalDate.of(1985, 8, 15));
		identitePivot.setBirthcountry("France");
		identitePivot.setBirthplace("Junit");
		identitePivot.setSub(UUID.randomUUID().toString());
		return identitePivot;
	}
}
