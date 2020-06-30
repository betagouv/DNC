import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import Card from 'react/components/card/Card';
import { Container, CardContent } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
import GenericDemarcheHeader from 'react/components/embedded/GenericDemarcheHeader';
import GenericDemarcheSummary from 'react/components/embedded/GenericDemarcheSummary';
import { Redirect } from 'react-router-dom';
import pdf from 'assets/pdf/justificatif_restauration_scolaire.pdf';
import EmbeddedAuthenticationSopes from 'react/components/authentication/EmbeddedAuthenticationSopes';
import RestaurantScolaireSelectData from './RestaurantScolaireSelectData';
import RestaurantScolaireProvideIdentity from './RestaurantScolaireProvideIdentity';
import RestaurantScolaireCheckInfo from './RestaurantScolaireCheckInfo';

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

const RestaurantScolaire = (props) => {
  const classes = useStyles();

  let content = null;
  let currentStep = 0;
  const steps = [
    { title: "Numéro d'allocataire", path: 'identity' },
    { title: 'Bénéficiaire & adresse', path: 'select' },
    { title: 'Vérification des informations', path: 'check' },
    { title: 'Transmission des données', path: 'summary' },
  ];
  const title = "J'inscris mon enfant en restauration scolaire";
  const codeDemarche = 'restaurantscolaire';

  switch (props.match.params.step) {
    case 'check':
      currentStep = 2;

      content = <RestaurantScolaireCheckInfo />;

      break;
    case 'summary':
      currentStep = 3;
      content = (
        <>
          <GenericDemarcheSummary pdf={pdf} data={props.restaurantScolaireInfo} />
        </>
      );
      break;
    case 'select':
      currentStep = 1;

      if (!props.numeroAllocataire || !props.codePostal) {
        return (
          <Redirect
            to={{
              pathname: 'identity',
            }}
          />
        );
      }

      if (
        !props.tokenInfo
        || !props.location.state
        || !props.location.state.embeddedWithScopes
        || props.tokenInfo.expirationTimestamp < Date.now()
      ) {
        return (
          <EmbeddedAuthenticationSopes
            redirectPath={`/${codeDemarche}/select`}
            codeDemarche={codeDemarche}
          />
        );
      }

      content = <RestaurantScolaireSelectData />;

      break;
    case 'identity':
      currentStep = 0;

      content = <RestaurantScolaireProvideIdentity />;

      break;
    default:
      return (
        <Redirect
          to={{
            pathname: `${props.match.url}/identity`,
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

RestaurantScolaire.propTypes = {
  match: PropTypes.shape({
    params: PropTypes.shape({
      step: PropTypes.string.isRequired,
    }).isRequired,
    url: PropTypes.string.isRequired,
  }).isRequired,
  numeroAllocataire: PropTypes.string.isRequired,
  codePostal: PropTypes.string.isRequired,
  // eslint-disable-next-line react/forbid-prop-types
  restaurantScolaireInfo: PropTypes.objectOf(
    PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  ).isRequired,
  tokenInfo: PropTypes.objectOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number]))
    .isRequired,
  location: PropTypes.shape({
    state: PropTypes.string.isRequired,
  }).isRequired,
};

RestaurantScolaire.defaultProps = {};

const mapStateToProps = state => ({
  numeroAllocataire: state.demarches.numeroAllocataire,
  codePostal: state.demarches.codePostal,
  restaurantScolaireInfo: state.demarches.restaurantScolaireInfo,
  tokenInfo: state.connection.tokenInfo,
  enfantsInfo: state.demarches.enfantsInfo,
  adresses: state.demarches.adresses,
});

export default connect(mapStateToProps)(enhancer(RestaurantScolaire));
