package fr.gouv.modernisation.dinum.dnc.franceconnect.factory;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenFranceconnect;
import org.meanbean.lang.Factory;

import java.util.UUID;

public class TokenFranceconnectFactory implements Factory<TokenFranceconnect> {

	@Override
	public TokenFranceconnect create() {
		TokenFranceconnect token = new TokenFranceconnect();
		token.setAccessToken(UUID.randomUUID().toString());
		token.setExpiresIn(60);
		token.setIdToken(UUID.randomUUID().toString());
		token.setTokenType("access_token");
		return token;
	}
}
