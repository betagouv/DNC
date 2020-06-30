package fr.gouv.modernisation.dinum.dnc.common.constante;

/**
 * Constantes pour la queue des Démarches
 */
public class DemarcheQueueConstantes {

	/**
	 * Nom du topic pour la queue des démarches
	 */
	public static final String DEMARCHE_TOPIC_EXCHANGE_NAME = "demarche";

	/**
	 * Nom de la queue des démarches
	 */
	public static final String DEMARCHE_QUEUE_NAME = "demarche-queue";

	/**
	 * Routing Key des messages liés aux démarches
	 */
	public static final String DEMARCHE_BASE_ROUTING_KEY = "demarche";


	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * Indique l'opération lié au message
	 */
	public static final String MAP_MESSAGE_KEY_OPERATION = "operation";

	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * Indique l'opération lié au message
	 */
	public static final String VALUE_OPERATION_CREATION = "CREATION";

	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * Indique l'opération lié au message
	 */
	public static final String VALUE_OPERATION_MISE_A_JOUR = "MISE_A_JOUR";

	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * Identifiant des données de la démarche partagé avec le partenaire
	 */
	public static final String MAP_MESSAGE_KEY_ID_DEMARCHE = "idDemarche";

	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * Identifiant de l'Usager
	 */
	public static final String MAP_MESSAGE_KEY_ID_USAGER = "idUsager";

	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * SIRET du Partenaire utilisant la démarche
	 */
	public static final String MAP_MESSAGE_KEY_SIRET_PARTENAIRE = "siretPartenaire";

	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * Code de la démarche
	 */
	public static final String MAP_MESSAGE_KEY_CODE_DEMARCHE = "codeDemarche";

	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * Nouveau statut de la démarche
	 */
	public static final String MAP_MESSAGE_KEY_STATUT = "statut";

	/**
	 * Clé dans la {@link java.util.Map} du message envoyé dans la queue.
	 * Commentaires associée à la mise à jour de la démarche
	 */
	public static final String MAP_MESSAGE_KEY_COMMENTAIRES = "commentaires";

	/**
	 * Constructeur par défaut privé pour chaché sa visibilité
	 */
	private DemarcheQueueConstantes() {
		//Constructeur par défaut privé pour chaché sa visibilité
	}

}
