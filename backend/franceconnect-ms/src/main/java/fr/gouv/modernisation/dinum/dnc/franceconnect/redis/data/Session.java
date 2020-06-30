package fr.gouv.modernisation.dinum.dnc.franceconnect.redis.data;

import fr.gouv.modernisation.dinum.dnc.common.utils.UsagerUtils;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenFranceconnect;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Objet stocké en base Redis pour stocker les données d'une session du DNC.
 * L'objet a un TimeToLive de 15 minutes en base Redis
 */
@RedisHash(value = Session.NAMESPACE_REDIS)
public class Session {

	/**
	 * Namespace des objets dans Redis
	 */
	public static final String NAMESPACE_REDIS = "Session";

	/**
	 * Identifiant de la session
	 */
	@Id
	private String id = null;

	/**
	 * Identifiant de l'usager
	 */
	private String idUsager = null;

	/**
	 * {@link TokenFranceconnect} associé à la session.
	 */
	private TokenFranceconnect tokenFranceconnect;

	/**
	 * {@link IdentitePivot} associé à la session
	 */
	private IdentitePivot identitePivot;

	/**
	 * Constructeur par défaut
	 */
	private Session(){
		//Constructeur par défaut pour permettre les tests
	}

	/**
	 * Constructeur paramétrée
	 * @param tokenFranceconnect token franceconnect rattaché à la session
	 * @param identitePivot identité pivot rattachée à la session
	 */
	public Session(TokenFranceconnect tokenFranceconnect, IdentitePivot identitePivot) {
		Assert.notNull(tokenFranceconnect, "Le Token Franceconnect ne peut pas être null");

		this.id = UUID.randomUUID().toString();
		this.tokenFranceconnect = tokenFranceconnect;
		this.identitePivot = identitePivot;
		this.idUsager = UsagerUtils.getUsagerIdFromIdentitePivot(identitePivot);
	}

	/**
	 * Getter du champ id
	 * return {@link String} la valeur du champ id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter du champ id
	 * @param id valeur à setter
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Getter du champ idUsager
	 * return {@link String} la valeur du champ idUsager
	 */
	public String getIdUsager() {
		return idUsager;
	}

	/**
	 * Setter du champ idUsager
	 *
	 * @param idUsager valeur à setter
	 */
	public void setIdUsager(String idUsager) {
		this.idUsager = idUsager;
	}

	/**
	 * Getter du champ tokenFranceconnect
	 * return {@link TokenFranceconnect} la valeur du champ tokenFranceconnect
	 */
	public TokenFranceconnect getTokenFranceconnect() {
		return tokenFranceconnect;
	}

	/**
	 * Setter du champ tokenFranceconnect
	 *
	 * @param tokenFranceconnect valeur à setter
	 */
	public void setTokenFranceconnect(TokenFranceconnect tokenFranceconnect) {
		this.tokenFranceconnect = tokenFranceconnect;
	}

	/**
	 * Getter du champ identitePivot
	 * return {@link IdentitePivot} la valeur du champ identitePivot
	 */
	public IdentitePivot getIdentitePivot() {
		return identitePivot;
	}

	/**
	 * Setter du champ identitePivot
	 *
	 * @param identitePivot valeur à setter
	 */
	public void setIdentitePivot(IdentitePivot identitePivot) {
		this.identitePivot = identitePivot;
	}
}
