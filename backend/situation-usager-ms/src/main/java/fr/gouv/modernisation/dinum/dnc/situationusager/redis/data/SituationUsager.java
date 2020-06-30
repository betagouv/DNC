package fr.gouv.modernisation.dinum.dnc.situationusager.redis.data;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Beneficiaire;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declaration;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Famille;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Objet représentant les informations du citoyen.
 * Mise à part l'ID généré par le DNC, l'intégralité des données vient d'autres systèmes que situation-usager-ms :
 * <ul>
 * <li>Franceconnect-MS pour l'identité pivot</li>
 * <li>API Franceconnectées</li>
 * <li>API non franceconnectées comme la CNAM ou la DGFIP</li>
 * </ul>
 */
@RedisHash(value = SituationUsager.NAMESPACE_REDIS)
public class SituationUsager implements Serializable {

	/**
	 * Namespace dans Redis
	 */
	public static final String NAMESPACE_REDIS = "SituationUsager";

	/**
	 * Identifiant unique du citoyen, créé à partir de son identité pivot
	 */
	@Id
	private String id;

	/**
	 * Identifiant de la démarche ayant entrainé la récupération des données de l'usager
	 */
	private String idDemarche;

	/**
	 * {@link SessionUsager} associé à la session
	 */
	private SessionUsager sessionUsager;

	/**
	 * {@link Beneficiaire} associé au compte Franceconnect de l'usager.
	 * Source : API CNAM (Franceconnectée)
	 */
	private Beneficiaire beneficiaireCnam;

	/**
	 * Informations de la CAF (non franceconnecté)
	 */
	private Famille famille;

	/**
	 * Informations de la DGFIP (non franceconnecté)
	 */
	private Declaration declaration;

	/**
	 * Constructeur par défaut
	 */
	private SituationUsager() {
		// Constructeur par défaut pour permettre des tests
	}

	/**
	 * Constructeur de la classe {@link SituationUsager}, à minima la situation ne peut exister
	 * que par l'existence d'une {@link SessionUsager} correspondante.
	 * @param sessionUsager la session de l'usager
	 */
	public SituationUsager(SessionUsager sessionUsager) {
		Assert.notNull(sessionUsager, "Le Token Franceconnect ne peut pas être null");

		this.id = sessionUsager.getUserId();
		this.sessionUsager = sessionUsager;
	}

	/**
	 * Getter du champ idCitoyen
	 * return {@link String} la valeur du champ idCitoyen
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter du champ idCitoyen
	 *
	 * @param id valeur à setter
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Getter du champ idDemarche
	 * return {@link String} la valeur du champ idDemarche
	 */
	public String getIdDemarche() {
		return idDemarche;
	}

	/**
	 * Setter du champ idDemarche
	 *
	 * @param idDemarche valeur à setter
	 */
	public void setIdDemarche(String idDemarche) {
		this.idDemarche = idDemarche;
	}

	/**
	 * Getter du champ identitePivot
	 * return {@link IdentitePivot} la valeur du champ identitePivot
	 */
	public SessionUsager getSessionUsager() {
		return sessionUsager;
	}

	/**
	 * Setter du champ identitePivot
	 *
	 * @param sessionUsager valeur à setter
	 */
	public void setSessionUsager(SessionUsager sessionUsager) {
		this.sessionUsager = sessionUsager;
	}

	/**
	 * Getter du champ beneficiaireCnam
	 * return {@link Beneficiaire} la valeur du champ beneficiaireCnam
	 */
	public Beneficiaire getBeneficiaireCnam() {
		return beneficiaireCnam;
	}

	/**
	 * Setter du champ beneficiaireCnam
	 *
	 * @param beneficiaireCnam valeur à setter
	 */
	public void setBeneficiaireCnam(Beneficiaire beneficiaireCnam) {
		this.beneficiaireCnam = beneficiaireCnam;
	}

	/**
	 * Getter du champ famille
	 * return {@link Famille} la valeur du champ famille
	 */
	public Famille getFamille() {
		return famille;
	}

	/**
	 * Setter du champ famille
	 *
	 * @param famille valeur à setter
	 */
	public void setFamille(Famille famille) {
		this.famille = famille;
	}

	/**
	 * Getter du champ declaration
	 * return {@link Declaration} la valeur du champ declaration
	 */
	public Declaration getDeclaration() {
		return declaration;
	}

	/**
	 * Setter du champ declaration
	 *
	 * @param declaration valeur à setter
	 */
	public void setDeclaration(Declaration declaration) {
		this.declaration = declaration;
	}
}
