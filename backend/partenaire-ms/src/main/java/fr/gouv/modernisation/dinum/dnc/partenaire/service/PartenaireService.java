package fr.gouv.modernisation.dinum.dnc.partenaire.service;

import fr.gouv.modernisation.dinum.dnc.partenaire.data.entity.Credential;
import fr.gouv.modernisation.dinum.dnc.partenaire.data.entity.Partenaire;
import fr.gouv.modernisation.dinum.dnc.partenaire.data.repository.CredentialRepository;
import fr.gouv.modernisation.dinum.dnc.partenaire.data.repository.PartenaireRepository;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.Credentials;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.PartenaireInfos;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service pour les opérations sur les partenaires
 */
@Transactional
@Service
public class PartenaireService {
	/**
	 * Logger {@link Log}
	 */
	private static final Log LOGGER = LogFactory.getLog(PartenaireService.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PartenaireRepository partenaireRepository;

	@Autowired
	private CredentialRepository credentialRepository;

	public List<Partenaire> findAll(){
		return partenaireRepository.findAll();
	}


	/**
	 * Création d'un partenaire en base de données.
	 * Les nouveaux partenaires n'ont aucun {@link Credential} associé.
	 * @param partenaireInfos les informations du partenaire
	 * @return {@link Partenaire} créée
	 * @throws ResponseStatusException Si on essaye de créer des infos de partenaires avec un SIRET existant en base.
	 */
	public Partenaire createPartenaire(PartenaireInfos partenaireInfos) {
		Assert.notNull(partenaireInfos, "Les informations du partenaire ne peuvent être null");
		Assert.notNull(partenaireInfos.getSiret(), "Le SIRET du partenaire ne peuvent être null");
		Assert.isTrue(partenaireInfos.getSiret().matches("[0-9]{14}"), "Le SIRET du partenaire doit être une suite de 14 chiffres.");

		// Check de l'existance
		Optional<Partenaire> existingPartenaire = partenaireRepository.findBySiret(partenaireInfos.getSiret());
		if(existingPartenaire.isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Le SIRET %s possède déjà des informations de partenaires.", partenaireInfos.getSiret()));
		}

		Partenaire partenaire = new Partenaire();
		BeanUtils.copyProperties(partenaireInfos, partenaire, "id");
		partenaire.setDateCreation(LocalDateTime.now());

		LOGGER.info(String.format("Création d'un partenaire : siret : %s", partenaire.getSiret()));

		return partenaireRepository.save(partenaire);
	}

	/**
	 * Wrapper de {@link #saveOrUpdateCredentialsForPartenaire(String, String, String, String)} avec l'objet {@link Credentials}.
	 * @param siret SIRET du partenaire concernée
	 * @param credential les Credentials (clientID/clientSecret/OldSecret) à utiliser pour valider l'opération
	 * @return l'objet {@link Credential} créé ou mise à jour, sinon {@code null}
	 */
	public Credential saveOrUpdateCredentialsForPartenaire(String siret, @NotNull Credentials credential){
		return saveOrUpdateCredentialsForPartenaire(siret, credential.getClientId(),
				credential.getClientSecret(),
				credential.getOldClientSecret());
	}

	/**
	 * Méthode permettant de créer ou de mettre à jour les credentials d'un partenaire.
	 * Renvoie {@code null} si une erreur est survenu lors du check des Credentials ou du Partenaire.
	 * @param siret SIRET du partenaire concernée
	 * @param clientId clientID du partenaire
	 * @param clientSecret secret à tester
	 * @param oldSecret Ancien secret à utiliser, uniquement en cas de mise à jour
	 * @return l'objet {@link Credential} créé ou mise à jour, sinon {@code null}
	 */
	public Credential saveOrUpdateCredentialsForPartenaire(String siret, String clientId, String clientSecret, String oldSecret){
		Credential credential = null;

		if(StringUtils.isNotBlank(oldSecret)
				&& credentialRepository.findByPartenaireSiretAndClientId(siret, clientId).isPresent()) {
			credential = checkCredentials(siret, clientId, oldSecret);
		}

		if(Objects.isNull(credential)) {
			Optional<Partenaire> optionalPartenaire = partenaireRepository.findBySiret(siret);
			if(optionalPartenaire.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						String.format("Tentative de sauvegarde de credentials pour un partenaire inexistant : siret : %s", siret)
				);
			}
			Partenaire partenaire = optionalPartenaire.get();

			credential = new Credential();
			credential.setClientId(clientId);
			credential.setClientSecret(passwordEncoder.encode(clientSecret));
			credential.setDateCreation(LocalDateTime.now());
			credential.setPartenaire(partenaire);
			LOGGER.info(String.format("Création de credentials pour un partenaire : siret : %s, clientID : %s", siret, clientId));
		}
		else {
			credential.setClientSecret(passwordEncoder.encode(clientSecret));
			credential.setDateMAJ(LocalDateTime.now());
			LOGGER.info(String.format("Mise à jour des credentials pour un partenaire : siret : %s, clientID : %s", siret, clientId));
		}

		return credentialRepository.save(credential);
	}

	/**
	 * Vérifie que le {@code clientSecret} correspond bien au secret du correspondant aux {@link Credential}
	 * associé au SIRET du partenaire et au clientID
	 * @param siretPartenaire SIRET du partenaire
	 * @param clientId clientID du partenaire
	 * @param clientSecret secret à tester
	 * @return si le {@code clientSecret} correspond bien à la base de donnée, l'objet {@link Credential} correspondant sinon {@code null}
	 */
	public Credential checkCredentials(String siretPartenaire, String clientId, String clientSecret) {
		Optional<Credential> optional = credentialRepository.findByPartenaireSiretAndClientId(siretPartenaire, clientId);

		if(optional.isEmpty()) {
			LOGGER.error(String.format("Aucun crédential trouvée pour le partenaire : siret : %s, ClientID : %s", siretPartenaire, clientId));
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
						String.format("Mauvaise données d'authentification : siret : %s, ClientID : %s", siretPartenaire, clientId));
		}
		boolean matchingClientSecret = passwordEncoder.matches(clientSecret, optional.get().getClientSecret());
		if(!matchingClientSecret) {
			LOGGER.error(String.format("Mauvais Secret pour le partenaire : siret : %s, ClientID : %s", siretPartenaire, clientId));
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					String.format("Mauvaise données d'authentification : siret : %s, ClientID : %s", siretPartenaire, clientId)
			);
		}

		return optional.get();
	}
}
