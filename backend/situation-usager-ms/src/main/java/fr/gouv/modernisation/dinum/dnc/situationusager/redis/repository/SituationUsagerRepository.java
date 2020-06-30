package fr.gouv.modernisation.dinum.dnc.situationusager.redis.repository;

import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository des {@link SituationUsager}
 */
@Repository
public interface SituationUsagerRepository extends CrudRepository<SituationUsager, String> {
}
