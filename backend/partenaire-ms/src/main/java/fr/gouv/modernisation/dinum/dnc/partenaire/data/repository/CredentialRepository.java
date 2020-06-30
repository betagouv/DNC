package fr.gouv.modernisation.dinum.dnc.partenaire.data.repository;

import fr.gouv.modernisation.dinum.dnc.partenaire.data.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour les objets {@link Credential}
 */
@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {

	/**
	 * Check l'existense du client ID/ClientSecret
	 * @param siretPartenaire SIRET du partenaire
	 * @param cliendId client ID du partenaire
	 * @return {@link Optional} d'objet {@link Credential} si l'objet existe ou non
	 */
	Optional<Credential> findByPartenaireSiretAndClientId(String siretPartenaire, String cliendId);
}
