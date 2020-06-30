package fr.gouv.modernisation.dinum.dnc.situationusager.redis.repository;

import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository des {@link DemarcheUsager}
 */
@Repository
public interface DemarcheUsagerRepository extends CrudRepository<DemarcheUsager, UUID> {
}
