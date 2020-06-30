package fr.gouv.modernisation.dinum.dnc.partenaire.data.repository;

import fr.gouv.modernisation.dinum.dnc.partenaire.data.entity.Partenaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour les objets {@link Partenaire}
 */
@Repository
public interface PartenaireRepository extends JpaRepository<Partenaire, Long> {

	/**
	 * Retrouver un partenaire par son SIRET
	 * @param siret SIRET du Partenaire
	 * @return {@link Optional} correspondant au contenu de la base.
	 */
	Optional<Partenaire> findBySiret(String siret);
}
