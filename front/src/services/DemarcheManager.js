import Logger from 'utils/Logger';
import StationnementUsagerInfoFactory from 'models/factories/demarches/StationnementUsagerInfoFactory';
import adressesUsagerInfo from 'assets/mocks/demarches/adresses_usager_info.mock.json';
import axios from 'axios';
import carteStationnementSocieteUsagerInfo from 'assets/mocks/demarches/carte_stationnement_societe_usager_info.mock.json';
import carteStationnementUsagerInfo from 'assets/mocks/demarches/carte_stationnement_usager_info.mock.json';
import enfantsUsagerInfo from 'assets/mocks/demarches/enfants_usager_info.mock.json';
import restaurantScolaireUsagerInfo from 'assets/mocks/demarches/restaurant_scolaire_usager_info.mock.json';
import scopesMock from 'assets/mocks/demarches/fetch_demarche_and_scopes.mock.json';
import societeAdressesUsagerInfo from 'assets/mocks/demarches/societe_adresses_usager_info.mock.json';
import societesUsagerInfo from 'assets/mocks/demarches/societes_usager_info.mock.json';
import stationnementUsagerInfo from 'assets/mocks/demarches/stationnement_usager_info.mock.json';
import vehiculesUsagerInfo from 'assets/mocks/demarches/vehicules_usager_info.mock.json';
import UsagerManager from './UsagerManager';

const fs = require('browserify-fs');

/**
 * @param {string} idDemarche - L'API Key décrivant la démarche en cours.
 *
 * @returns {any} An object { scopes, demarche }.
 */
async function fetchScopes(/* codeDemarche */) {
  let scopes = [];

  try {
    /* if (!process.env.USE_MOCKS) {
      const response = (await http.get(`/scopes/${codeDemarche}`)).body;

      scopes = response.status;
    } else { */
    scopes = scopesMock.scopes;
    // }
  } catch (error) {
    Logger.error(error);
  }

  return scopes;
}

/**
 *
 * @returns {any} An object { scopes, demarche }.
 */
async function fetchDemarcheAndScopes() {
  let scopes = [];
  let demarche;

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncScopesApiUri = process.env.DNC_SCOPES_API_URI;

      const response = (await http.get(dncScopesApiUri)).query({ id_demarche: idDemarche }).body;

      scopes = response.scopes;
      demarche = response.demarche;
    } else { */
    scopes = scopesMock.scopes;
    demarche = scopesMock.demarche;
    // }
  } catch (error) {
    Logger.error(error);
  }

  return { scopes, demarche };
}

/**
 *
 * @returns {any} - Les infos usager concernant la démarche donnée.
 */
async function fetchStationnementUsagerInfo(/* idDemarche, accessToken */) {
  const usagerInfo = new StationnementUsagerInfoFactory();

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncDemarcheUsagerInfoApiUri = process.env.DNC_DEMARCHE_USAGER_INFO_API_URI;

      const response = (
        await http
          .get(dncDemarcheUsagerInfoApiUri)
          .query({ id_demarche: idDemarche })
          .set({ Authorization: `Bearer ${accessToken}` })
      ).body;

      usagerInfo = response;
    } else { */
    usagerInfo.prenoms = stationnementUsagerInfo.prenoms;
    usagerInfo.nomFamille = stationnementUsagerInfo.nom_famille;
    usagerInfo.adresse = stationnementUsagerInfo.adresse;
    usagerInfo.communeReference = stationnementUsagerInfo.commune_reference;
    usagerInfo.typeVehicule = stationnementUsagerInfo.type_vehicule;
    usagerInfo.immatriculationVehicule = stationnementUsagerInfo.immatriculation_vehicule;
    usagerInfo.addresseJustifiee = stationnementUsagerInfo.addresse_justifiee;
    // }
  } catch (error) {
    Logger.error(error);
  }

  return usagerInfo;
}

/**
 * @param numeroAllocataire
 * @param codePostal
 * @param props
 * @param token
 * @returns {any} - Les infos usager concernant la démarche donnée.
 */
async function fetchRestaurantScolaireUsagerInfo(
  numeroAllocataire,
  codePostal,
  props /* , token */,
) {
  const usagerInfo = {};

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncDemarcheUsagerInfoApiUri = process.env.DNC_DEMARCHE_USAGER_INFO_API_URI;

      const response = (
        await http.get(dncDemarcheUsagerInfoApiUri).set({ Authorization: `Bearer ${token}` })
      ).body;

      usagerInfo = response;
    } else { */
    usagerInfo.quotientFamilial = restaurantScolaireUsagerInfo.quotient_familial;
    usagerInfo.revenuFiscalRef = restaurantScolaireUsagerInfo.revenu_fiscal_ref;
    usagerInfo.numeroAllocataire = numeroAllocataire;
    usagerInfo.codePostal = codePostal;
    usagerInfo.adresseEnfant = props.adresse.value;
    usagerInfo.nomEnfant = props.enfant.nom_famille;
    usagerInfo.prenomsEnfant = props.enfant.prenoms;
    usagerInfo.dateNaissanceEnfant = props.enfant.date_naissance;
    // }
  } catch (error) {
    Logger.error(error);
  }

  return usagerInfo;
}

/**
 *
 * @param {string} idDemarche - La clé d'API DNC.
 * @param {string} accessToken - Le token d'accès FC.
 *
 * @returns {any} - Les infos usager concernant la démarche donnée.
 */
async function fetchEnfantsUsagerInfo(/* idDemarche, accessToken */) {
  let enfants = {};

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncDemarcheUsagerInfoApiUri = process.env.DNC_DEMARCHE_USAGER_INFO_API_URI;

      const response = (
        await http
          .get(dncDemarcheUsagerInfoApiUri)
          .query({ id_demarche: idDemarche })
          .set({ Authorization: `Bearer ${accessToken}` })
      ).body;

      usagerInfo = response;
    } else { */
    enfants
      = enfantsUsagerInfo.enfants; /* .map(enfant => ({
      id: enfant.id,
      value: `${enfant.nom_famille} ${enfant.prenoms}`,
    })); */
    // }
  } catch (error) {
    Logger.error(error);
  }

  return enfants;
}

/**
 *
 * @param {string} idDemarche - La clé d'API DNC.
 * @param {string} accessToken - Le token d'accès FC.
 *
 * @returns {any} - Les infos usager concernant la démarche donnée.
 */
async function fetchAdressesUsagerInfo(/* idDemarche, accessToken */) {
  let adresses = {};

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncDemarcheUsagerInfoApiUri = process.env.DNC_DEMARCHE_USAGER_INFO_API_URI;

      const response = (
        await http
          .get(dncDemarcheUsagerInfoApiUri)
          .query({ id_demarche: idDemarche })
          .set({ Authorization: `Bearer ${accessToken}` })
      ).body;

      usagerInfo = response;
    } else { */
    adresses = adressesUsagerInfo.adresses;
    // }
  } catch (error) {
    Logger.error(error);
  }

  return adresses;
}

/**
 * @param {string} idDemarche - La clé d'API DNC.
 * @param siret
 * @param {string} accessToken - Le token d'accès FC.
 * @returns {any} - Les infos usager concernant la démarche donnée.
 */
async function fetchSocieteAdressesUsagerInfo(/* idDemarche, accessToken, siret */) {
  let societeAdresses = {};

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncDemarcheUsagerInfoApiUri = process.env.DNC_DEMARCHE_USAGER_INFO_API_URI;

      const response = (
        await http
          .get(dncDemarcheUsagerInfoApiUri)
          .query({ id_demarche: idDemarche })
          .set({ Authorization: `Bearer ${accessToken}` })
      ).body;

      usagerInfo = response;
    } else { */
    societeAdresses = societeAdressesUsagerInfo.adresses;
    // }
  } catch (error) {
    Logger.error(error);
  }

  return societeAdresses;
}

/**
 * @param props
 * @param siret
 * @param vehiculeSociete
 */
async function fetchCarteStationnementUsagerInfo(props, siret /* , token */) {
  const usagerInfo = {};

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncDemarcheUsagerInfoApiUri = process.env.DNC_DEMARCHE_USAGER_INFO_API_URI;

      const response = (
        await http.get(dncDemarcheUsagerInfoApiUri).set({ Authorization: `Bearer ${token}` })
      ).body;

      usagerInfo = response;
    } else { */

    if (siret) {
      usagerInfo.raisonSociale = carteStationnementSocieteUsagerInfo.raisonSociale;
      usagerInfo.siret = siret;
    }

    usagerInfo.adresse = props.adresse ? props.adresse.value : undefined;
    usagerInfo.nom = carteStationnementUsagerInfo.nom;
    usagerInfo.prenoms = carteStationnementUsagerInfo.prenoms;
    usagerInfo.immatriculation = props.vehicule.immatriculation;
    usagerInfo.modele = props.vehicule.modele;

    if (props.vehicule.electrique) {
      usagerInfo.vehiculeElectrique = 'Oui';
    }

    // }
  } catch (error) {
    Logger.error(error);
  }

  return usagerInfo;
}

/**
 *
 * @param {string} idDemarche - La clé d'API DNC.
 * @param {string} accessToken - Le token d'accès FC.
 *
 * @returns {any} - Les infos usager concernant la démarche donnée.
 */
async function fetchVehiculesUsagerInfo(/* idDemarche, accessToken */) {
  let vehicules = {};

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncDemarcheUsagerInfoApiUri = process.env.DNC_DEMARCHE_USAGER_INFO_API_URI;

      const response = (
        await http
          .get(dncDemarcheUsagerInfoApiUri)
          .query({ id_demarche: idDemarche })
          .set({ Authorization: `Bearer ${accessToken}` })
      ).body;

      usagerInfo = response;
    } else { */
    vehicules = vehiculesUsagerInfo.vehicules;
    // }
  } catch (error) {
    Logger.error(error);
  }

  return vehicules;
}

/**
 *
 * @param {string} idDemarche - La clé d'API DNC.
 * @param {string} accessToken - Le token d'accès FC.
 *
 * @returns {any} - Les infos usager concernant la démarche donnée.
 */
async function fetchSocietesUsagerInfo(/* idDemarche, accessToken */) {
  let societes = {};

  try {
    /* if (!process.env.USE_MOCKS) {
      const dncDemarcheUsagerInfoApiUri = process.env.DNC_DEMARCHE_USAGER_INFO_API_URI;

      const response = (
        await http
          .get(dncDemarcheUsagerInfoApiUri)
          .query({ id_demarche: idDemarche })
          .set({ Authorization: `Bearer ${accessToken}` })
      ).body;

      usagerInfo = response;
    } else { */
    societes = societesUsagerInfo.societes;
    // }
  } catch (error) {
    Logger.error(error);
  }

  return societes;
}

/**
 * @param idSessios
 * @param siret
 */
async function fetchData(idSessios, siret = 21920040900015) { // Mairie d'Issy-les-moulineaux
  const raw3 = `{\"siret\":\"${siret}\",\"raisonSociale\":\"SARL Dupont et Fils\",\"siretPartenaire\":\"${siret}\"}`;

  const headers = {
    'Content-Type': 'application/json',
    'DNC-ID-SESSION': idSessios,
    'X-Correlation-ID': '2dd11dde-fb44-498e-8357-be7dd54479dc',
  };

  axios.post('http://alternate-back-ms-sums-dnc-dev.cloudapps.dfp.ovh/embedded/data/CARTE_STATIONNEMENT', raw3, {
    headers,
  })
    .then((response) => {
      const event = new CustomEvent(
        'RECEIVED_DATA_FROM_BACK',
        {
          detail: [response, idSessios],
        },
      );
      document.dispatchEvent(event);
    });
}

/**
 * @param {string} token - Le token fc à utiliser.
 * @param {string} siret - Le siret reçu de la mairie fc à utiliser.
 *
 * @returns {any} Retourne le userId et l'Id session.
 */
async function fetchUserSession(token, siret) {
  let rep;
  UsagerManager.fetchIdentitePivot(token).then(() => {
    const raw = JSON.stringify({
      currentToken: token,
      identitePivot: {
        sub: 'ef46c77603814fc0afbc19ee8f94d2cada4f436d363fe1cdd5f8713298a6fce5v1',
        given_name: 'Angela Claire Louise',
        family_name: 'DUBOIS',
        birthdate: '1962-08-24',
        gender: 'female',
        birthplace: '75107',
        birthcountry: '99100',
        preferred_username: '',
        phone_number: null,
        email: null,
        address: {
          country: 'France',
          formatted: 'France Paris 75107 20 avenue de Ségur',
          locality: 'Paris',
          postal_code: '75107',
          street_address: '20 avenue de Ségur',
        },
      },
    });
    const myHeaders = new Headers();
    myHeaders.append('Content-Type', 'application/json');
    myHeaders.append('Accept', 'application/json');
    const requestOptions = {
      method: 'POST',
      headers: myHeaders,
      body: raw,
      redirect: 'follow',
    };
    fetch('http://back-ms-fms-dnc-dev.cloudapps.dfp.ovh/fc/front/user-session', requestOptions)
      .then(lala => lala.json())
      .then((reponse) => {
        rep = reponse;
        fetchData(rep.idSession, siret);
        return rep;
      })
      .catch(error => console.log('error', error));
  });
}

/**
 * @param {string} codeDemarche - Le token fc à utiliser.
 * @param {string} token - Le token fc à utiliser.
 *
 * @returns {any} An object { scopes, demarche }.
 */
async function fetchDataForDemarche(codeDemarche, token) {
  const myHeaders = new Headers();
  myHeaders.append('Content-Type', 'application/json');
  myHeaders.append('Accept', 'application/json');
  await fetchUserSession(token).then(repp => repp);
}

/**
 * @param {string} idSession - L'id de l'utilisateur.
 * @param {string} idDemarche - L'id de la demarche.
 * @param {string} etape - Le numero de l'etape en cours.
 * @param vehicule
 * @param addresse
 * @returns {any} Mise à jour des données sélectionnée.
 */
async function fetchUpdateDemarche(idSession, idDemarche, etape, vehicule, addresse) {
  let rep;

  const body = {
    adresseComplete: addresse.replace('é', '\u00e9'),
    vehiculeSelectionne: JSON.stringify(vehicule).replace(/"/g, '\"'),
  };

  // raw = `"${raw}"`;
  const headers = {
    'Content-Type': 'application/json',
    'DNC-ID-SESSION': idSession,
    'X-Correlation-ID': '14b781db-f77c-4216-bf7f-58809526571b',
  };

  axios.put(`https://back-ms-sums-dnc-dev.cloudapps.dfp.ovh/embedded/demarche/${idDemarche}?etape=${etape}`, body, {
    headers,
  })
    .then(() => {
      const event = new CustomEvent(
        'PUT_DATA_TO_BACK',
        {
          detail: [idDemarche, idSession],
        },
      );
      document.dispatchEvent(event);
    });
  return rep;
}

/**
 * @param {string} idSession - L'id de l'utilisateur.
 * @param {string} idDemarche - L'id de la demarche.
 *
 * @returns {any} Mise à jour des données sélectionnée.
 */
async function fetchJustificatif(idSession, idDemarche) {
  const headers = {
    'Content-Type': 'application/json',
    'DNC-ID-SESSION': idSession,
    responseType: 'blob',
  };
  axios.get(`https://back-ms-sums-dnc-dev.cloudapps.dfp.ovh/embedded/justificatif/${idDemarche}`, {
    headers,
  })
    .then((response) => {
      const responsePDF = response;
      fs.writeFile('result_base64.pdf', responsePDF.data.contenu, 'base64', (error) => {
        if (error) {
          throw error;
        } else {
          fs.readFile('result_base64.pdf', 'binary', (err, data) => {
            const event = new CustomEvent(
              'FETCH_JUSTIFICATIF',
              {
                detail: [data, response.data.contenu],
              },
            );
            document.dispatchEvent(event);
          });
        }
      });
      return responsePDF;
    });
}

/**
 * @param {string} idSession - L'id de l'utilisateur.
 * @param {string} idDemarche - L'id de la demarche.
 *
 * @returns {any} Mise à jour des données sélectionnée.
 */
async function fetchFinalizeDemarche(idSession, idDemarche) {
  let rep;

  const raw = JSON.stringify({
    non: 'important',
  });

  const headers = {
    'Content-Type': 'application/json',
    'DNC-ID-SESSION': idSession,
    'X-Correlation-ID': 'd9c65810-db86-4425-9dd2-375b566d8cf8',
  };

  axios.post(`https://back-ms-sums-dnc-dev.cloudapps.dfp.ovh/embedded/demarche/${idDemarche}`, raw, {
    headers,
  })
    .then(() => {
      const event = new CustomEvent(
        'DEMARCHE_FINALIZE',
        {
          detail: [idDemarche, idSession],
        },
      );
      document.dispatchEvent(event);
      fetchJustificatif(idSession, idDemarche);
    });
  return rep;
}

export default {
  fetchDemarcheAndScopes,
  fetchStationnementUsagerInfo,

  fetchRestaurantScolaireUsagerInfo,
  fetchEnfantsUsagerInfo,
  fetchAdressesUsagerInfo,

  fetchCarteStationnementUsagerInfo,
  fetchVehiculesUsagerInfo,
  fetchSocietesUsagerInfo,
  fetchSocieteAdressesUsagerInfo,

  fetchScopes,

  fetchUserSession,
  fetchDataForDemarche,
  fetchUpdateDemarche,
  fetchFinalizeDemarche,
  fetchJustificatif,

  fetchData,
};
