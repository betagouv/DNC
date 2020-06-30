import Logger from 'utils/Logger';
import UserFactory from 'models/factories/UserFactory';
import identitePivotMock from 'assets/mocks/informations/identite_pivot.mock.json';
import infosFamilleMock from 'assets/mocks/informations/infos_caf.mock.json';
import infosFiscalesMock from 'assets/mocks/informations/infos_dgfip.mock.json';
import infosPoleEmploiMock from 'assets/mocks/informations/infos_pole_emploi.mock.json';
import infosSecuMock from 'assets/mocks/informations/infos_cnam.mock.json';
import http from './http';

/**
 * Effectue une requète HTTP (GET) avec le token d'accès FranceConnect
 * afin de récupérer l'identié pivot de l'usager.
 *
 * @param {string} accessToken - Le token d'accès FranceConnect.
 *
 * @returns {Promise<any>} Promesse représentant l'identité pivot de l'usager.
 */
async function fetchIdentitePivot(accessToken) {
  const user = new UserFactory();

  try {
    let userInfo = {};

    if (!process.env.USE_MOCKS) {
      const fcUserInfoEndpoint = process.env.FC_URI + process.env.FC_USERINFO_ENDPOINT;
      userInfo = (
        await http
          .get(`${window.location.protocol}//${process.env.CORS_BYPASS}/${fcUserInfoEndpoint}`)
          .set({ Authorization: `Bearer ${accessToken}` })
      ).body;
      user.identitePivot.prenoms = userInfo.given_name;
      user.identitePivot.nomFamille = userInfo.family_name;
      user.identitePivot.dateNaissance = userInfo.birthdate;
      user.identitePivot.lieuNaissance = userInfo.birthplace;
      user.identitePivot.paysNaissance = userInfo.birthcountry;
      user.identitePivot.mail = 'angelaCLDubois@gmail.com'; // userInfo.mail
      user.identitePivot.telephone = '0606060606'; // userInfo.telephone;
      user.genre = userInfo.gender;
      if (userInfo.address) {
        user.identitePivot.pays = userInfo.address.country;
        user.identitePivot.ville = userInfo.address.locality;
        user.identitePivot.codePostal = userInfo.address.postal_code;
        user.identitePivot.adresse = userInfo.address.street_address;
      }
    } else {
      userInfo = identitePivotMock;

      user.identitePivot.prenoms = userInfo.given_name;
      user.identitePivot.nomFamille = userInfo.family_name;
      user.identitePivot.dateNaissance = userInfo.birthdate;
      user.identitePivot.lieuNaissance = userInfo.birthplace;
      user.identitePivot.paysNaissance = userInfo.birthcountry;
      user.identitePivot.pays = userInfo.address.country;
      user.identitePivot.ville = userInfo.address.locality;
      user.identitePivot.codePostal = userInfo.address.postal_code;
      user.identitePivot.adresse = userInfo.address.street_address;
      user.identitePivot.genre = 'femal';
    }
  } catch (error) {
    user.identitePivot = null;
    Logger.error(error);
  }

  return user.identitePivot;
}

/**
 * Effectue une requète HTTP (GET) avec le token d'accès FranceConnect
afin de récupérer les données fiscales de l'usager.
 *
 * @param numeroFiscal
 * @param referenceAvisFiscal
 * @param {string} accessToken - Le token d'accès FranceConnect.
 * @returns {Promise<any>} Promesse représentant les données fiscales de l'usager.
 */
async function fetchInfosFiscales(numeroFiscal, referenceAvisFiscal, accessToken) {
  let infosFiscales = {
    foyerFiscal: '',
    dateRecouvrement: '',
    dateEtablissement: '',
    nombreParts: '',
    situationFamiliale: '',
    nombrePersonnesCharge: '',
    revenuBrutGlobal: '',
    revenuImposable: '',
    impotRevenuNetAvantCorrections: '',
    montantImpot: '',
    rfr: '',
    anneeImpots: '',
    anneeRevenus: '',
    situationPartielle: '',
  };

  try {
    let data = {};

    if (!process.env.USE_MOCKS) {
      data = (await http.get('/api/InfoFiscales').set({ Authorization: `Bearer ${accessToken}` }))
        .body;
    } else {
      data = [infosFiscalesMock];
    }

    if (data.length > 0) {
      [data] = data;

      Object.keys(infosFiscales).forEach((key) => {
        infosFiscales[key] = data[key];
      });

      infosFiscales.numeroFiscal = numeroFiscal;
      infosFiscales.referenceAvisFiscal = referenceAvisFiscal;
      infosFiscales.revenuBrutGlobal = '48751'; // à supprimer
      infosFiscales.dateRecouvrement = '07/2020'; // à supprimer
    }
  } catch (error) {
    infosFiscales = null;
    Logger.error(error);
  }

  return infosFiscales;
}

/**
 * Effectue une requète HTTP (GET) avec le token d'accès FranceConnect
et le numéro d'allocataire de l'usager afin de récupérer les données famille de l'usager.
 *
 * @param {string} numeroAllocataire - Le numéro d'allocataire de l'usager.
 * @param codePostal
 * @param {string} accessToken - Le token d'accès FranceConnect.
 * @returns {Promise<any>} Promesse représentant les données famille de l'usager.
 */
async function fetchInfosFamille(numeroAllocataire, codePostal, accessToken) {
  let infosFamille = {};

  try {
    let data = {};

    if (!process.env.USE_MOCKS) {
      data = (
        await http
          .get('/api/InfoFamiliales')
          .set({ Authorization: `Bearer ${accessToken}` })
          .query(`numeroAllocataire=${numeroAllocataire}`)
      ).body;
    } else {
      data = [infosFamilleMock];
    }

    if (data.length > 0) {
      [data] = data;

      /* Object.keys(infosFamille).forEach(key => {
        infosFamille[key] = data[key];
      }); */

      infosFamille.quotientFamilial = data.quotientFamilial;
      infosFamille.adresse = data.adresse;
      infosFamille.nombreEnfants = data.nombreEnfants;
      infosFamille.numeroAllocataire = numeroAllocataire;
      infosFamille.codePostal = codePostal;
      infosFamille.mois = '04'; // data.mois
      infosFamille.annee = '2020'; // data.annee;

      const enfants = [];

      for (let i = 1; i <= data.nombreEnfants; i++) {
        enfants.push(data[`enfant${i}`]);
      }

      infosFamille.enfants = enfants;
    }
  } catch (error) {
    infosFamille = null;
    Logger.error(error);
  }

  return infosFamille;
}

/**
 * Effectue une requète HTTP (GET) avec le token d'accès FranceConnect
 * afin de récupérer les données sécurité sociale de l'usager.
 *
 * @param {string} accessToken - Le token d'accès FranceConnect.
 *
 * @returns {Promise<any>} Promesse représentant les données sécurité sociale de l'usager.
 */
async function fetchInfosSecu(accessToken) {
  let infosSecu = {
    nir: '',
    medecin_traitant: '',
    code_type_contrat: '',
    libelle_type_contrat: '',
    qualite: '',
    date_debut_droits: '',
    date_fin_droits: '',
    nature_assurance: '',
    adresse_centre: '',
    rang_naissance: '',
  };

  try {
    let data = {};

    if (!process.env.USE_MOCKS) {
      data = (
        await http.get('/api/InfoSecuSociale').set({ Authorization: `Bearer ${accessToken}` })
      ).body;
    } else {
      data = [infosSecuMock];
    }

    if (data.length > 0) {
      [data] = data;

      Object.keys(infosSecu).forEach((key) => {
        infosSecu[key] = data[key];
      });
    }
  } catch (error) {
    infosSecu = null;
    Logger.error(error);
  }
  infosSecu.rang_naissance = '1'; // à supprimer

  return infosSecu;
}

/**
 *
 * @param {string} accessToken - Le token d'accès FranceConnect.
 *
 * @returns {Promise<any>} Promesse représentant les données sécurité sociale de l'usager.
 */
async function fetchInfosPoleEmploi(/* accessToken */) {
  let infosPoleEmploi = {
    assuranceChomage: '',
    allocationSecurisationProfessionnelle: '',
    aideRetourEmploi: '',
    minimaSociaux: '',
    adresseAgence: '',
    horairesAgence: '',
    typeAgence: '',
  };

  try {
    let data = {};

    /* if (!process.env.USE_MOCKS) {
      data = (await http.get('/api/InfoPoleEmploi').set({ Authorization: `Bearer ${accessToken}` }))
        .body;
    } else { */
    data = [infosPoleEmploiMock];
    // }

    if (data.length > 0) {
      [data] = data;

      Object.keys(infosPoleEmploi).forEach((key) => {
        infosPoleEmploi[key] = data[key];
      });
    }
  } catch (error) {
    infosPoleEmploi = null;
    Logger.error(error);
  }

  return infosPoleEmploi;
}

/**
 *
 *
 * @param {string} accessToken - Le token d'accès FranceConnect.
 *
 * @returns {Promise<any>} Promesse représentant.
 */
async function fetchDemarchesInfos(accessToken) {
  let infosDemarches = [];

  const dossiers = [
    {
      libelle_demarche: 'Inscription liste électorale - Démarches simplifiées',
      sources: "Ministère de l'Intérieur",
      documents_en_attente: [],
      alerte_etat: false,
      alerte_documents: false,
    },
    {
      libelle_demarche: 'Inscription stationnement - Démarches simplifiées',
      sources: "Ministère de l'Intérieur",
      documents_en_attente: [],
      alerte_etat: true,
      alerte_documents: false,
    },
    {
      libelle_demarche: 'Inscription au service de restauration - Démarches Simplifiées',
      sources: 'Finances Publiques',
      documents_en_attente: ['1 justificatif domicile'],
      alerte_etat: false,
      alerte_documents: true,
    },
    {
      libelle_demarche: 'Inscription au service de petite enfance - Démarches Simplifiées',
      sources: 'CAF',
      documents_en_attente: [],
      alerte_etat: false,
      alerte_documents: false,
    },
  ];

  try {
    let data = {};

    if (!process.env.USE_MOCKS) {
      data = (await http.get('/api/dossiers').set({ Authorization: `Bearer ${accessToken}` })).body;
    } else {
      data = {
        dossiers: [
          {
            id: 1126457,
            updated_at: '2019-11-28T10:55:55.052Z',
            state: 'without_continuation',
            initiated_at: '2019-11-28T10:52:45.044Z',
          },
          {
            id: 1126458,
            updated_at: '2019-11-28T10:55:09.923Z',
            state: 'refused',
            initiated_at: '2019-11-28T10:52:56.509Z',
          },
          {
            id: 1126460,
            updated_at: '2019-11-28T10:55:01.717Z',
            state: 'closed',
            initiated_at: '2019-11-28T10:53:04.339Z',
          },
          {
            id: 1172339,
            updated_at: '2019-12-10T16:14:13.185Z',
            state: 'closed',
            initiated_at: '2019-12-10T15:33:44.091Z',
          },
        ],
        pagination: { page: 1, resultats_par_page: 100, nombre_de_page: 1 },
      };
    }

    if (data && data.dossiers && data.dossiers.length > 0) {
      data = data.dossiers;

      dossiers.forEach((dossier, index) => {
        if (index >= data.length) {
          return;
        }

        infosDemarches.push({
          ...dossier,
          ...data[index],
        });
      });
    }
  } catch (error) {
    infosDemarches = null;
    Logger.error(error);
  }

  return infosDemarches;
}

export default {
  fetchIdentitePivot,
  fetchInfosFiscales,
  fetchInfosFamille,
  fetchInfosSecu,
  fetchDemarchesInfos,
  fetchInfosPoleEmploi,
};
