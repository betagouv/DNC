package fr.gouv.modernisation.dinum.dnc.demarche.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.modernisation.dinum.dnc.common.constante.DemarcheQueueConstantes;
import fr.gouv.modernisation.dinum.dnc.common.exception.NotFoundException;
import fr.gouv.modernisation.dinum.dnc.demarche.data.entity.Demarche;
import fr.gouv.modernisation.dinum.dnc.demarche.data.enumeration.Statut;
import fr.gouv.modernisation.dinum.dnc.demarche.data.repository.DemarcheRepository;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DemarcheService {

    /**
     * Logger {@link Logger}
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DemarcheService.class);

    /**
     * Description pour l'exception {@link NotFoundException}
     */
    public static final String ERREUR_NOT_FOUND_DEMARCHE = "Démarche non trouvée";

    /**
     * Repository pour l'accès aux données {@link Demarche}
     */
    @Autowired
    private DemarcheRepository demarcheRepository;

    /**
     * Méthode de création d'une démarche
     * @param idDemarche identifiant au format {@link UUID} de la démarche. Utilisé par le partenaire
     * @param idUsager identifiant de l'Usager créé à partir de son identité pivot
     * @param siretPartenaire SIRET du partenaires
     * @param codeDemarche Code de la démarche
     * @return objet {@link Demarche} créé et persisté en base.
     */
    public Demarche createDemarche(String idDemarche, String idUsager, String siretPartenaire, String codeDemarche) {
        Demarche demarcheEntity = new Demarche();

        demarcheEntity.setIdDemarche(idDemarche);
        demarcheEntity.setIdUsager(idUsager);
        demarcheEntity.setSiretPartenaire(siretPartenaire);
        demarcheEntity.setCodeDemarche(codeDemarche);

        //Initialisation des champs
        demarcheEntity.setStatut("Créée");
        demarcheEntity.setDateDebut(LocalDateTime.now());

        // Sauvegarde et renvoie
        return demarcheRepository.save(demarcheEntity);
    }

    /**
     * Récupère une démarche à partir du triplet (id Démarche, id Usager, SIRET Partenaire).
     * Lève une exception {@link NotFoundException} si la démarche correspondante n'est pas trouvée.
     * @param idDemarche identifiant au format {@link UUID} de la démarche. Utilisé par le partenaire
     * @param siretPartenaire SIRET du partenaires
     * @param statut Nouveau statut donnée à la démarche par le partenaire
     * @param commentaires commentaires du nouveau statut si il y en a un
     * @return l'objet {@link Demarche} correspondant
     */
    public Demarche updateDemarche(String idDemarche, String siretPartenaire, String statut, String commentaires) {
        Optional<Demarche> optional = demarcheRepository.findByIdDemarcheAndSiretPartenaire(idDemarche,siretPartenaire);

        if(optional.isEmpty()) {
            String tripletIds = String.format("id Démarche : %s, SIRET Partenaires : %s", idDemarche, siretPartenaire);
            LOGGER.error("Aucune démarche n'a été trouvé pour le triplet : {}",
                    tripletIds);
            throw new NotFoundException(ERREUR_NOT_FOUND_DEMARCHE, tripletIds);
        }

        Demarche demarche = optional.get();
        demarche.setStatut(statut);
        demarche.setCommentaires(StringUtils.substring(commentaires, 0, 255));
        demarche.setDateMiseAJour(LocalDateTime.now());

        return optional.get();
    }

    /**
     * Crée une démarche et la sauvegarde en base.
     * La démarche sera au statut présentant dans l'objet en paramètre, sinon au statut {@link Statut#CREEE}
     * @param demarche les données de la démarche
     * @return l'objet {@link Demarche} créé
     */
    public Demarche createDemarche(fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche demarche) {
        Demarche demarcheEntity = new Demarche();

        // Copie des données Source
        BeanUtils.copyProperties(demarche, demarcheEntity, "id");
        // Copie de l'identifiant de démarche
        demarcheEntity.setIdDemarche(demarche.getId());

        // Gestion du statut
        String statut = demarche.getStatut();
        if(statut == null) {
            statut = "Créée";
        }
        demarcheEntity.setStatut(statut);

        // Initialisation des dates
        demarcheEntity.setDateDebut(LocalDateTime.now());

        demarcheEntity.setIdUsager(demarche.getIdUsager());
        demarcheEntity.setSiretPartenaire(demarche.getPartenaireID());

        // Sauvegarde et renvoie
        return demarcheRepository.save(demarcheEntity);
    }

    /**
     * Mise à jour d'une {@link Demarche} à partir du {@link fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche#id(String)}
     * Lève une {@link NotFoundException} si aucun objet {@link Demarche} n'est trouvée
     * @param demarche les données de la démarche à mettre à jour
     * @return l'objet {@link Demarche} correspondant et mis à jour
     */
    public Demarche updateDemarche(@Valid fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche demarche) {
        Optional<Demarche> optionalDemarche = demarcheRepository.findById(Long.valueOf(demarche.getId()));

        if(optionalDemarche.isEmpty()) {
            throw new NotFoundException(ERREUR_NOT_FOUND_DEMARCHE, demarche.getId());
        }

        Demarche demarcheEntity = optionalDemarche.get();

        String statut = demarche.getStatut();
        if(statut == null) {
            statut = "Mise à jour";
        }
        demarcheEntity.setStatut(statut);
        demarcheEntity.setDateMiseAJour(LocalDateTime.now());

        return demarcheRepository.save(demarcheEntity);
    }

    /**
     * Renvoie les données d'une démarche via un objet {@link fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche}
     * Lève une {@link NotFoundException} si l'identifiant ne correpsond à aucune démarche.
     * @param id identifiant de la démarche
     * @return l'objet {@link fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche} correspondant à la démarche
     */
    public fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche getDemarche(String id) {
        Optional<Demarche> optionalDemarche = demarcheRepository.findById(Long.valueOf(id));

        if(optionalDemarche.isEmpty()) {
            throw new NotFoundException(ERREUR_NOT_FOUND_DEMARCHE, id);
        }

        fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche demarcheRest = new fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche();
        BeanUtils.copyProperties(optionalDemarche.get(), demarcheRest, "id");

        demarcheRest.setPartenaireID(optionalDemarche.get().getSiretPartenaire());

        // Copie des champs non géré automatiquement
        demarcheRest.setId(optionalDemarche.get().getIdDemarche());
        demarcheRest.setStatut(optionalDemarche.get().getStatut());

        return demarcheRest;
    }

    /**
     * Méthode de gestion des messages.
     * Délègue la gestion en fonction de l'opération (CREATION ou MISE_A_JOUR)
     * @param message {@link Map} représentation des données du message.
     */
    @JmsListener(destination = DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME)
    public void receiveMessage(Message message, Session session) throws JMSException {
        LOGGER.info("Réception du message : CorrelationID : {}, Message : {}, Session : {}",message.getJMSCorrelationID(), message , session);
        String correlationId = message.getJMSCorrelationID();

        Map<String, String> mapMessage = extractMapFromMessage(correlationId, message);
        if(!mapMessage.containsKey(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION)) {
            LOGGER.warn("Pas d'opération spécifiée dans le message : CorrelationID : {}, l'opération par défaut est la création", message.getJMSCorrelationID());
            return;
        }

        if(StringUtils.equals(DemarcheQueueConstantes.VALUE_OPERATION_CREATION, mapMessage.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION))) {
            receiveMessageCreation(correlationId, mapMessage);
        }
        else if(StringUtils.equals(DemarcheQueueConstantes.VALUE_OPERATION_MISE_A_JOUR,mapMessage.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION))) {
            receiveMessageUpdate(correlationId, mapMessage);
        }
    }

    /**
     * Extrait la {@link Map} d'un {@link Message} reçu via la fil JMS de {@link DemarcheQueueConstantes#DEMARCHE_QUEUE_NAME}.
     * Les exceptions {@link IOException} et {@link JMSException} sont catchés et la méthode renvoie une {@link Map} vide.
     *
     * @param correlationId {@link String} Correlation ID du message
     * @param message {@link Message} message reçu via la fil JMS
     * @return la {@link Map} correspondante au message, sinon une {@link Map} vide ({@link Map#of()})
     */
    private Map<String, String> extractMapFromMessage(String correlationId, Message message) {

        try {
            if(message instanceof ActiveMQTextMessage) {
                LOGGER.info("Lecture d'un Message de type ActiveMQTextMessage : CorrelationID : {}, Message : {}", message.getJMSCorrelationID(),message);
                ObjectMapper objectMapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                        .configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
                        ;
                TypeReference<Map<String,String>> typeRef
                        = new TypeReference<>() {};
                return objectMapper.readValue(((ActiveMQTextMessage) message).getText(), typeRef);
            }
            else {
                LOGGER.info("Lecture d'un Message de type inconnu : CorrelationID : {}, Message : {}", message.getJMSCorrelationID(),message);
                return message.getBody(Map.class);
            }
        }
        catch (JMSException e) {
            LOGGER.error(String.format("Erreur à la lecture du Message : Correlation ID : %s", correlationId), e);
        }
        catch (IOException e) {
            LOGGER.error(String.format("Erreur lors du processing du contenu d'un Message de démarche reçu : Correlation ID : %s", correlationId), e);
        }

        return Map.of();
    }

    /**
     * Méthode de réception du message de création
     * @param correlationId {@link String} Correlation ID du message
     * @param message {@link Map} représentation des données du message.
     */
    public void receiveMessageCreation(String correlationId, Map<String, String> message) {
        LOGGER.info("Gestion d'un message de création : CorrelationID : {}, Map du Message : {}", correlationId, message);

        if( !message.containsKey(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE)
                && !message.containsKey(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_USAGER)
                && !message.containsKey(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE)
                && !message.containsKey(DemarcheQueueConstantes.MAP_MESSAGE_KEY_CODE_DEMARCHE)
        ) {
            LOGGER.error("Le message (CorrelationID : {}) ne contient pas toutes les infos nécessaires à la création d'une démarche", correlationId);
            return;
        }


        LOGGER.info("Création d'une démarche associée à un message avec les partenaires : CorrelationID : {}, id Démarche : {}, id Usager : {}, SIRET Partenaires : {}, code de démarche : {}" ,
                correlationId,
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_USAGER),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_CODE_DEMARCHE)
        );
        Demarche demarche = createDemarche(
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_USAGER),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_CODE_DEMARCHE)
        );
        LOGGER.info("Création d'une démarche : CorrelationID : {}, id : {}, id Démarche : {}, id Usager : {}, SIRET Partenaire : {} ",
                correlationId,
                demarche.getId(),
                demarche.getIdDemarche(),
                demarche.getIdUsager(),
                demarche.getSiretPartenaire());
        demarcheRepository.flush();
    }

    /**
     * Méthode de réception du message de mise à jour
     * @param correlationId {@link String} Correlation ID du message
     * @param message {@link Map} représentation des données du message.
     */
    public void receiveMessageUpdate(String correlationId, Map<String, String> message) {
        LOGGER.info("Gestion d'un message de mise à jour : CorrelationID : {}, Map du Message : {}", correlationId, message);

        if( !message.containsKey(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE)
                && !message.containsKey(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE) ) {
            LOGGER.error("Le message (CorrelationID : {}) ne contient pas toutes les infos nécessaires à la création d'une démarche", correlationId);
            return;
        }


        LOGGER.info("Mise à jour d'une démarche associée à un message avec les paramètres : CorrelationID : {}, id Démarche : {}, SIRET Partenaires : {}, Statut : {}, Commentaires : {}" ,
                correlationId,
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_STATUT),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_COMMENTAIRES)
        );
        Demarche demarche = updateDemarche(
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_STATUT),
                message.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_COMMENTAIRES)
        );
        LOGGER.info("Mise à jour d'une démarche : CorrelationID : {}, id : {}, id Démarche : {}, id Usager : {}, SIRET Partenaire : {}, Statut : {} ",
                correlationId,
                demarche.getId(),
                demarche.getIdDemarche(),
                demarche.getIdUsager(),
                demarche.getSiretPartenaire(),
                demarche.getStatut());
        demarcheRepository.flush();
    }

}
