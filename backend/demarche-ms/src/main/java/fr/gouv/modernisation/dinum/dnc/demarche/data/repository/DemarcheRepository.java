package fr.gouv.modernisation.dinum.dnc.demarche.data.repository;

import fr.gouv.modernisation.dinum.dnc.demarche.data.entity.Demarche;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository des {@link Demarche}
 */
public interface DemarcheRepository extends JpaRepository<Demarche, Long> {

	/**
	 * Recherche d'une {@link Demarche} à partir de l'id de l'usager, l'id du partenaire et l'id fonctionnelle de la
	 * démarche.
	 * @param partenaireId id du partenaire
	 * @param demarcheId id fonctionnelle de la démarche
	 * @return {@link Demarche} correspondante sinon {@link Optional#empty()}
	 */
	Optional<Demarche> findByIdDemarcheAndSiretPartenaire(String partenaireId, String demarcheId);
}
