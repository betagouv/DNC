import { Grid, Hidden } from '@material-ui/core';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import DataList from 'react/components/list/DataList';
import PropTypes from 'prop-types';
import React from 'react';
import ReactResizeDetector from 'react-resize-detector';
import ameliLogo from 'assets/images/ameli_logo.png';
import cafLogo from 'assets/images/caf_logo.png';
import { connect } from 'react-redux';
import dgfipLogo from 'assets/images/dgfip_logo.png';
import fcLogo from 'assets/images/fc_logo.png';
import { identitePivotShape } from 'models/shapes/IdentitePivotShape';
import poleEmploiLogo from 'assets/images/pole_emploi_logo.png';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import InformationCard from './cards/InformationCard';
import FamilleInformationCardData from './cards/FamilleInformationCardData';
import EmptyFiscaleInformationCard from './cards/EmptyFiscaleInformationCard';
import EmptyFamilleInformationCard from './cards/EmptyFamilleInformationCard';

const enhancer = compose(pure);

const useStyles = makeStyles({
  grid: {
    margin: 0,
    maxWidth: '100%',
  },
  emptyGridItem: {
    padding: '1.5rem',
  },
});

let seeMorePoleEmploi = [];
let seeMoreInfoFiscal = [];
let seeMoreFamille = [];

/**
 * Formate les données de l'identité pivot (label + value) afin de les afficher.
 *
 * @param {*} identitePivot - L'objet contenant les données de l'identité pivot.
 * @returns {Array} - Une array d'objets.
 */
function formatIdentitePivot(identitePivot) {
  let adresse = [
    identitePivot.adresse,
    identitePivot.codePostal,
    identitePivot.ville,
    identitePivot.pays,
  ];

  adresse = adresse.filter(adresseElement => adresseElement && adresseElement !== '');
  return [
    {
      label: 'nom',
      value: identitePivot.nomFamille,
    },
    {
      label: 'prénoms',
      value: identitePivot.prenoms,
    },
    {
      label: 'né(e) le',
      value: identitePivot.dateNaissance,
    },
    {
      label: 'né(e) à',
      value: identitePivot.lieuNaissance,
    },
    {
      label: 'pays',
      value: identitePivot.paysNaissance,
    },
    {
      label: 'adresse',
      value: adresse.length > 0 ? adresse.join(' ') : null,
    },
    {
      label: 'mail',
      value: identitePivot.mail,
    },
    {
      label: 'telephone',
      value: identitePivot.telephone,
    },
  ];
}

/**
 * Formate les données fiscales (label + value) afin de les afficher.
 *
 * @param {*} infosFiscales - L'objet contenant les données fiscales.
 * @returns {Array} - Une array d'objets.
 */
function formatInfosFiscales(infosFiscales) {
  seeMoreInfoFiscal = [];
  seeMoreInfoFiscal.push({
    label: 'revenu fiscal de réf.',
    value: infosFiscales.rfr,
  },
  {
    label: 'date mise en recouvrement',
    value: infosFiscales.dateRecouvrement,
  });
  return [
    {
      label: 'numéro fiscal',
      value: infosFiscales.numeroFiscal,
    },
    {
      label: 'revenu brut global',
      value: infosFiscales.revenuBrutGlobal,
    },
    {
      label: 'Revenu imposable',
      value: infosFiscales.revenuImposable,
    },
    {
      label: 'Montant impots',
      value: infosFiscales.montantImpot,
    },
    {
      label: 'foyer fiscal',
      value: infosFiscales.foyerFiscal,
    },
    {
      label: 'situation familiale',
      value: infosFiscales.situationFamiliale,
    },
    {
      label: 'nbr de personnes à charge',
      value: infosFiscales.nombrePersonnesCharge,
    },
  ];
}

/**
 * Formate les données famille (label + value) afin de les afficher.
 *
 * @param {*} infosFamille - L'objet contenant les données famille.
 * @returns {Array} - Une array d'objets.
 */
function formatInfosFamille(infosFamille) {
  seeMoreFamille = [];
  seeMoreFamille.push({
    label: 'adresse',
    value: infosFamille.adresse,
  },
  {
    label: 'code postal',
    value: infosFamille.codePostal,
  });
  return [
    {
      label: 'numéro allocataire',
      value: infosFamille.numeroAllocataire,
    },
    {
      label: 'quotient familial',
      value: infosFamille.quotientFamilial,
    },
    {
      label: 'date quotient familial',
      value: `${infosFamille.mois}/${infosFamille.annee}`,
    },
    {
      label: 'enfants à charge',
      value: infosFamille.nombreEnfants,
    },
    {
      label: 'enfants',
      value: infosFamille.enfants,
    },
  ];
}

/**
 * Formate les données sécurité sociale (label + value) afin de les afficher.
 *
 * @param {*} infosSecu - L'objet contenant les données sécurité sociale.
 * @returns {Array} - Une array d'objets.
 */
function formatInfosSecu(infosSecu) {
  return [
    {
      label: 'numéro de sécurité sociale',
      value: infosSecu.nir,
    },
    {
      label: 'médecin traitant',
      value: infosSecu.medecin_traitant,
    },
    {
      label: "nature de l'assurance",
      value: infosSecu.nature_assurance,
    },
    {
      label: 'adresse du centre',
      value: infosSecu.adresse_centre,
    },
    {
      label: 'rang naissance',
      value: infosSecu.rang_naissance,
    },
  ];
}

/**
 * Formate les données sécurité sociale (label + value) afin de les afficher.
 *
 * @param {*} infosPoleEmploi - Info PoleEMploi.
 * @returns {Array} - Une array d'objets.
 */
function formatInfosPoleEmploi(infosPoleEmploi) {
  seeMorePoleEmploi = [];
  seeMorePoleEmploi.push({
    label: 'Allocation de sécurisation professionnelle',
    value: infosPoleEmploi.allocationSecurisationProfessionnelle,
  });
  seeMorePoleEmploi.push({
    label: "Aide au retour à l'emploi",
    value: infosPoleEmploi.aideRetourEmploi,
  },
  {
    label: 'Minima sociaux',
    value: infosPoleEmploi.minimaSociaux,
  });
  return [
    {
      label: 'Assurance chômage',
      value: infosPoleEmploi.assuranceChomage,
    },
    {
      label: "adresse de l'agence",
      value: infosPoleEmploi.adresseAgence,
    },
    {
      label: "horaires de l'agence",
      value: infosPoleEmploi.horairesAgence,
    },
    {
      label: "type d'agence",
      value: infosPoleEmploi.typeAgence,
    },
  ];
}

const InformationsGrid = (props) => {
  const matches = useMediaQuery('(min-width:1280px)');

  const classes = useStyles({ matches });
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('sm'));

  // Le nombre de cartes vides à ajouter
  const [emptyCards, setEmptyCards] = React.useState(0);

  const content = [];

  // Le nombre total de cartes (utiliser pour calculer le nombre de cartes vides)
  let cardsNb = 0;

  // La largeur min de chaque cartes (en rem)
  let cardMinWidth = 25;

  // Si taille écran < 600px => on change la largeur min des cartes (de 25rem à 18.75rem)
  if (xsDown) {
    cardMinWidth = 18.75;
  }

  if (props.identitePivot) {
    content.push(
      <InformationCard
        id="test"
        title="Mes informations personnelles"
        source="France Connect"
        logoSrc={fcLogo}
        minWidth={`${cardMinWidth}rem`}
      >
        <DataList
          labelWidth="40%"
          valueWidth="60%"
          data={formatIdentitePivot(props.identitePivot)}
        />
      </InformationCard>,
    );
  }

  if (
    props.infosFiscales
    && props.infosFiscales.numeroFiscal
    && props.infosFiscales.referenceAvisFiscal
  ) {
    const data = formatInfosFiscales(props.infosFiscales);
    content.push(
      <InformationCard
        title="Mes informations fiscales"
        source="Finances Publiques"
        logoSrc={dgfipLogo}
        actionButtonTarget="https://cfspart.impots.gouv.fr/LoginAccess"
        minWidth={`${cardMinWidth}rem`}
        seeMore={seeMoreInfoFiscal}
      >
        <DataList
          labelWidth="40%"
          valueWidth="60%"
          data={data}
        />
      </InformationCard>,
    );
  } else {
    content.push(
      <EmptyFiscaleInformationCard
        title="Mes informations fiscales"
        source="Finances Publiques"
        logoSrc={dgfipLogo}
        fetchDataCallback={props.fetchInfosFiscales}
        minWidth={`${cardMinWidth}rem`}
      />,
    );
  }

  if (props.infosFamille) {
    const data = formatInfosFamille(props.infosFamille);
    content.push(
      <InformationCard
        title="Mes informations familiales"
        source="CAF"
        logoSrc={cafLogo}
        actionButtonTarget="https://wwwd.caf.fr/wps/portal/caffr/login"
        minWidth={`${cardMinWidth}rem`}
        seeMore={seeMoreFamille}
      >
        <FamilleInformationCardData data={data} />
      </InformationCard>,
    );
  } else {
    content.push(
      <EmptyFamilleInformationCard
        title="Mes informations familiales"
        source="CAF"
        logoSrc={cafLogo}
        informationLabel="Afin de pouvoir récupérer vos données, vous devez entrer votre
        <b>Numéro d'allocataire"
        fetchDataCallback={props.fetchInfosFamille}
        minWidth={`${cardMinWidth}rem`}
      />,
    );
  }

  if (props.infosSecu) {
    content.push(
      <InformationCard
        title="Mes informations sécurité sociale"
        source="CPAM"
        logoSrc={ameliLogo}
        actionButtonTarget="https://assure.ameli.fr/PortailAS/FranceConnect"
        minWidth={`${cardMinWidth}rem`}
      >
        <DataList labelWidth="40%" valueWidth="60%" data={formatInfosSecu(props.infosSecu)} />
      </InformationCard>,
    );
  }

  if (props.infosPoleEmploi) {
    const data = formatInfosPoleEmploi(props.infosPoleEmploi);
    content.push(
      <InformationCard
        title="Mes informations Pôle Emploi"
        source="Pôle Emploi"
        logoSrc={poleEmploiLogo}
        actionButtonTarget="https://authentification-candidat.pole-emploi.fr/connexion/XUI/#login/&realm=%2Findividu&goto=https%3A%2F%2Fauthentification-candidat.pole-emploi.fr%3A443%2Fconnexion%2Foauth2%2Fauthorize%3Frealm%3D%252Findividu%26response_type%3Did_token%2520token%26scope%3Dopenid%2520idRci%2520profile%2520contexteAuthentification%2520email%2520courrier%2520notifications%2520etatcivil%2520logW%2520individu%2520pilote%2520nomenclature%2520coordonnees%2520navigation%2520reclamation%2520prdvl%2520idIdentiteExterne%2520pole_emploi%2520suggestions%2520actu%2520application_USG_PN073-tdbcandidat_6408B42F17FC872440D4FF01BA6BAB16999CD903772C528808D1E6FA2B585CF2%26client_id%3DUSG_PN073-tdbcandidat_6408B42F17FC872440D4FF01BA6BAB16999CD903772C528808D1E6FA2B585CF2%26state%3DZk4YB8AQftwoJNBE%26nonce%3DSLkqykd7Pf4xeLFa%26redirect_uri%3Dhttps%253A%252F%252Fcandidat.pole-emploi.fr%252Fespacepersonnel%252F"
        minWidth={`${cardMinWidth}rem`}
        seeMore={seeMorePoleEmploi}
      >
        <DataList
          labelWidth="40%"
          valueWidth="60%"
          data={data}
        />
      </InformationCard>,
    );
  }

  cardsNb = content.length;

  // On rajouter un certain nombre de cartes vides pour l'affichage
  for (let i = 0; i < emptyCards; i++) {
    content.push(
      <Grid item xs className={classes.emptyGridItem} implementation="css" component={Hidden} />,
    );
  }

  /**
   * Callback appelée lorsque la largeur du composant Grid change.
   * Cette fonction permet de calculer le nombre de Card vide à ajouter
   * afin que toutes les Card (même seules sur une ligne) aient la même largeur.
   *
   * @param {*} width - Largeur du composant Grid.
   */
  const onResize = (width) => {
    let oneRemValuePx = 16; // Par défaut 1rem = 16px

    if (window.innerWidth < 1600 && window.innerWidth >= 1280) {
      // (0.64625 * 16 + 0.11000000000000014 * window.innerWidth)
      // formule pour calculer la valeur actuelle en px de 1rem.
      // Voir fichier main.scss
      // oneRemValuePx = 0.64625 * 16 + (0.11000000000000014 * window.innerWidth) / 100;
      oneRemValuePx = -0.27000000000000046 * 16 + (1.2750000000000006 * window.innerWidth) / 100;
    }

    if (window.innerWidth < 1280 && window.innerWidth >= 600) {
      // (0.64625 * 16 + 0.11000000000000014 * window.innerWidth)
      // formule pour calculer la valeur actuelle en px de 1rem.
      // Voir fichier main.scss
      // oneRemValuePx = 0.64625 * 16 + (0.11000000000000014 * window.innerWidth) / 100;
      oneRemValuePx = 0.6329044117647059 * 16 + (0.14558823529411768 * window.innerWidth) / 100;
    }

    // Nombre de carte par ligne = Taille de la Grid / min-width d'une carte
    const cardsNbByLines = Math.trunc(width / (cardMinWidth * oneRemValuePx));

    // Nombre de ligne = nombre de carte / nombre de carte par ligne
    const lineNb = cardsNb / cardsNbByLines;

    if (lineNb % 1 !== 0 && cardsNbByLines < cardsNb) {
      // Si lineNb est pas rond => lineNb % 1 = 0
      // Nombre de carte vide à ajouter =
      // nombre de ligne (arrondi au plus haut) * nombre de cartes par ligne - nombre de cartes
      setEmptyCards(Math.ceil(lineNb) * cardsNbByLines - cardsNb);
    } else if (emptyCards > 0) {
      setEmptyCards(0);
    }
  };

  return (
    <>
      <Grid handleWidth container justify="center" className={classes.grid}>
        <ReactResizeDetector handleWidth handleHeight onResize={onResize} />

        {content}
      </Grid>
    </>
  );
};

InformationsGrid.propTypes = {
  identitePivot: identitePivotShape.isRequired,
  infosFamille: PropTypes.objectOf(PropTypes.string).isRequired,
  infosFiscales: PropTypes.objectOf(PropTypes.string).isRequired,
  infosSecu: PropTypes.objectOf(PropTypes.string).isRequired,
  infosPoleEmploi: PropTypes.objectOf(PropTypes.string).isRequired,
  fetchInfosFamille: PropTypes.func,
  fetchInfosFiscales: PropTypes.func,
};

InformationsGrid.defaultProps = {
  fetchInfosFamille: () => {},
  fetchInfosFiscales: () => {},
};

const mapStateToProps = state => ({
  isHeaderVisible: state.connection.showHeader,
  identitePivot: state.identity.identitePivot,
  infosFiscales: state.identity.infosFiscales,
  infosFamille: state.identity.infosFamille,
  infosSecu: state.identity.infosSecu,
  infosDemarches: state.identity.infosDemarches,
  infosPoleEmploi: state.identity.infosPoleEmploi,
});

const mapDispatchToProps = () => ({});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(InformationsGrid));
