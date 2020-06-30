package fr.gouv.modernisation.dinum.dnc.demarche.controller;

import fr.gouv.modernisation.dinum.dnc.demarche.data.entity.Demarche;
import fr.gouv.modernisation.dinum.dnc.demarche.generated.api.server.DemarcheApi;
import fr.gouv.modernisation.dinum.dnc.demarche.service.DemarcheService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * Controller surchargeant le code généré pour exposer le microservice de gestion des démarches.
 *
 * @author Sopra Steria Group
 */
@Controller
public class DemarcheController implements DemarcheApi {

    /**
     * Logger {@link Log}
     */
    private static final Log logger = LogFactory.getLog(DemarcheController.class);

    /**
     * Service qui permet de récupérer les données auprès des fournisseurs de données.
     * <p>
     * {@link DemarcheService}
     */
    @Autowired
    private DemarcheService demarcheService;

    /**
     * @see fr.gouv.modernisation.dinum.dnc.demarche.generated.api.server.DemarcheApi#createDemarche(fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche)
     */
    @Override
    public ResponseEntity<String> createDemarche(fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche demarche) {
        logger.debug("Création d'une démarche");
        Demarche demarcheDO = demarcheService.createDemarche(demarche);


        return new ResponseEntity<>(demarcheDO.getId().toString(), HttpStatus.CREATED);
    }

    /**
     * @see fr.gouv.modernisation.dinum.dnc.demarche.generated.api.server.DemarcheApi#getDemarche(String)
     */
    @Override
    public ResponseEntity<fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche> getDemarche(String id) {
        logger.debug("Récupération de la démarche: " + id);
        fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche demarche = demarcheService.getDemarche(id);
        return new ResponseEntity<>(demarche, HttpStatus.OK);
    }

    /**
     * @see fr.gouv.modernisation.dinum.dnc.demarche.generated.api.server.DemarcheApi#updateDemarche(fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche)
     */
    @Override
    public ResponseEntity<String> updateDemarche(fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche demarche) {
        logger.debug("Mise à jour de la démarche : " + demarche.getId());
        Demarche demarcheDO = demarcheService.updateDemarche(demarche);
        return new ResponseEntity<>(demarcheDO.getId().toString(), HttpStatus.OK);
    }

}
