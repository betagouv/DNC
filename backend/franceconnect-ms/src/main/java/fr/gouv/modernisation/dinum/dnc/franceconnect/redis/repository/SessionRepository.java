package fr.gouv.modernisation.dinum.dnc.franceconnect.redis.repository;

import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.data.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour les objets {@link Session}
 */
@Repository
public interface SessionRepository extends CrudRepository<Session, String> {

}
