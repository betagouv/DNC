package fr.gouv.modernisation.dinum.dnc.franceconnect.factory;

import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.data.Session;
import org.meanbean.lang.Factory;

public class SessionFactory implements Factory<Session> {

	@Override
	public Session create() {
		return new Session(new TokenFranceconnectFactory().create(), new IdentitePivotFactory().create());
	}

}
