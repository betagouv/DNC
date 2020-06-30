import { CardContent, Container } from '@material-ui/core';
import { compose, pure } from 'recompose';

import Card from 'react/components/card/Card';
import DemarcheManager from 'services/DemarcheManager';
import GenericDemarcheHeader from 'react/components/embedded/GenericDemarcheHeader';
import GenericDemarcheSummary from 'react/components/embedded/GenericDemarcheSummary';
import PropTypes from 'prop-types';
import React from 'react';
import { Redirect } from 'react-router-dom';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import pdf from 'assets/pdf/justificatif_restauration_scolaire.pdf';
import { sessionService } from 'redux-react-session';
import CarteStationnementSelectData from './CarteStationnementSelectData';
import CarteStationnementCheckInfo from './CarteStationnementCheckInfo';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  card: {
    height: '75%',
    width: '95%',

    [theme.breakpoints.down('xs')]: {
      boxShadow: 'none',
      height: '100%',
      width: '100%',
    },
  },
  container: {
    padding: '1rem',
    margin: 0,
    maxWidth: '100%',
    height: '100%',
    // Workaroung pour corrigé un bug IE11 et Edge
    // height: '70%'
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  cardContent: {
    padding: '2rem',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    height: '100%',

    [theme.breakpoints.down('xs')]: {
      padding: '0 !important',
    },
  },
}));

/**
 * Récupère les info de la session en cours.
 *
 * @param {*} trueCodeDemarche - Le code de la demrache en cours.
 * @param {*} currentStep - L'etape actuelle.
 * @returns {*} Les infos à utiliser pour la démarche.
 */
async function fetchInfo(trueCodeDemarche, currentStep) {
  if (currentStep === 'select') {
    const Session = await sessionService.loadSession();
    DemarcheManager.fetchDataForDemarche(trueCodeDemarche, Session.tokenInfo.access_token);

    return Session.tokenInfo.access_token;
  }
  return null;
}

const CarteStationnement = (props) => {
  const classes = useStyles();
  const trueCodeDemarche = 'CARTE_STATIONNEMENT';
  let vehiculeList;
  let token;
  let currentStep = 0;
  fetchInfo(trueCodeDemarche, props.match.params.step).then((tok) => {
    token = tok;
  });
  document.addEventListener('RECEIVED_DATA_FROM_BACK', (event) => {
    vehiculeList = event.detail[0].data.vehicules;
  });

  let content = null;
  const steps = [
    { title: 'Véhicule & adresse', path: 'select' },
    { title: 'Vérification des informations', path: 'check' },
    { title: 'Transmission des données', path: 'summary' },
  ];
  const title = 'Je demande une carte de stationnement';
  const codeDemarche = 'cartestationnement';

  switch (props.match.params.step) {
    case 'check':
      currentStep = 1;

      content = <CarteStationnementCheckInfo />;

      break;
    case 'summary':
      currentStep = 2;
      content = (
        <>
          <GenericDemarcheSummary
            pdf={pdf}
            tokenDemarche={token}
            data={{ ...props.carteStationnementInfo, ...props.embeddedData }}
          />
        </>
      );
      break;
    case 'select':
      currentStep = 0;

      /* if (
        !props.tokenInfo ||
        !props.location.state ||
        !props.location.state.embeddedWithScopes ||
        props.tokenInfo.expirationTimestamp < Date.now()
      ) {
        return (
          <EmbeddedAuthenticationSopes
            redirectPath={`/${codeDemarche}/select`}
            codeDemarche={codeDemarche}
          />
        );
      } */

      content = <CarteStationnementSelectData vehicules={vehiculeList} />;

      break;
    default:
      return (
        <Redirect
          to={{
            pathname: `/${codeDemarche}/select`,
          }}
        />
      );
  }

  return (
    <Container className={classes.container}>
      <Card className={classes.card}>
        <CardContent className={classes.cardContent}>
          <GenericDemarcheHeader allSteps={steps} title={title} activeStep={currentStep} />
          {content}
        </CardContent>
      </Card>
    </Container>
  );
};

CarteStationnement.propTypes = {
  match: PropTypes.shape({
    params: PropTypes.shape({
      step: PropTypes.string.isRequired,
    }).isRequired,
  }).isRequired,
  carteStationnementInfo: PropTypes.objectOf(
    PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  ).isRequired,
  location: PropTypes.shape({
    state: PropTypes.string.isRequired,
  }).isRequired,
  embeddedData: PropTypes.objectOf(
    PropTypes.oneOf(PropTypes.string, PropTypes.number, PropTypes.bool),
  ),

};

CarteStationnement.defaultProps = {
  embeddedData: undefined,
};

const mapStateToProps = state => ({
  carteStationnementInfo:
    { ...state.demarches.carteStationnementInfo, adresse: state.demarches.adresse },
  tokenInfo: state.connection.tokenInfo,
  embeddedData: state.demarches.embeddedData,
});

export default connect(
  mapStateToProps,
)(enhancer(CarteStationnement));
