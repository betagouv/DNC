import {
  CardContent,
  Container,
  Slide,
  useMediaQuery,
} from '@material-ui/core';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import Card from 'react/components/card/Card';
import PropTypes from 'prop-types';
import React from 'react';
import { Redirect } from 'react-router-dom';
import Stepper from 'react/components/stepper/Stepper';
import colors from 'style/config.variables.scss';
import { connect } from 'react-redux';
import pdf from 'assets/pdf/justif_E.pdf';
import JustificatifsPersonnalisesSelection from './selection/JustificatifsPersonnalisesSelection';
import JustificatifsPersonnalisesModification from './modification/JustificatifsPersonnalisesModification';
import JustificatifsPersonnalisesFinalisation from './finalisation/JustificatifsPersonnalisesFinalisation';
import styles from './JustificatifsPersonnalises.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridContainer: {
    padding: '1rem 4.6875rem',
    maxWidth: '100%',
    // height: 'calc(100vh - 5rem)', // 5rem = header heigh
    display: 'flex',
    flexDirection: 'column',
  },
  card: {
    height: '100%',
  },

  /* root: {
    display: 'flex',
    flexDirection: 'column',
    height: '100%',
    alignItems: 'center',
    padding: '2rem',
  },
  card: { height: 'initial', width: '100%', maxWidth: '1000px' }, */
  icon: {
    height: 'auto',
    width: '3.75rem',
  },
  cardAction: {
    color: colors.darkishBlue,
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
  },
  cardContent: {
    height: '100%',
  },
  button: {
    marginTop: '1.5rem',
    marginBottom: '1.5rem',
    alignSelf: 'center',
  },
  headerCell: {
    fontSize: '0.75rem',
    fontWeight: 'bold',
    fontStretch: 'normal',
    fontStyle: 'normal',
    lineHeight: 'normal',
    letterSpacing: 'normal',
    color: colors.lightNavyBlue,
    height: '80px',
    marginTop: '16px',
  },
  checkBoxRoot: {
    color: colors.darkishBlue,
    '&:hover': {
      backgroundColor: colors.darkishBlueAlpha,
    },
    alignSelf: 'flex-end',
  },
  checkBoxChecked: {
    color: `${colors.darkishBlue} !important`,
    '&:hover': {
      backgroundColor: `${colors.darkishBlueAlpha} !important`,
    },
    alignSelf: 'flex-end',
  },
});

const JustificatifsPersonnalises = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  let stepContent = null;
  let currentStep = 0;
  const steps = [
    {
      title: 'Justificatif de votre démarche',
      path: 'selection',
    },
    {
      title: 'Données supplémentaires',
      path: 'modification',
    },
    {
      title: 'Justificatif finalisé',
      path: 'finalisation',
    },
  ];
  const title = 'Création de mon justificatif';
  let containerHeight;

  switch (props.match.params.step) {
    case 'selection':
      currentStep = 0;

      stepContent = (
        <JustificatifsPersonnalisesSelection
          defaultJustificatif={props.location.defaultJustificatif}
        />
      );

      break;

    case 'modification':
      currentStep = 1;

      stepContent = <JustificatifsPersonnalisesModification />;

      break;

    case 'finalisation':
      currentStep = 2;

      stepContent = <JustificatifsPersonnalisesFinalisation pdf={pdf} />;

      containerHeight = '75%';

      break;
    default:
      return (
        <Redirect
          to={{
            pathname: `${props.match.url}/selection`,
          }}
        />
      );
  }

  const content = (
    <>
      <p className={styles.cardTitle} style={{ marginBottom: '10px' }}>
        {title}
      </p>

      <Stepper
        activeStep={currentStep}
        allSteps={steps}
        style={{ paddingTop: '0' }}
        history={props.history}
      />

      {stepContent}
    </>
  );

  if (xsDown) {
    return (
      <>
        <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>{content}</div>
      </>
    );
  }

  return (
    <Slide direction="left" in mountOnEnter unmountOnExit>
      <Container className={classes.gridContainer} style={{ height: containerHeight }}>
        <Card className={classes.card}>
          <CardContent
            className={classes.cardContent}
            style={{ padding: '2rem 0 1rem', display: 'flex', flexDirection: 'column' }}
          >
            {content}
          </CardContent>
        </Card>
      </Container>
    </Slide>
  );
};

JustificatifsPersonnalises.propTypes = {
  match: PropTypes.shape({
    params: PropTypes.shape({
      step: PropTypes.string.isRequired,
    }).isRequired,
    url: PropTypes.string.isRequired,
  }).isRequired,
  location: PropTypes.shape({
    state: PropTypes.string.isRequired,
    defaultJustificatif: PropTypes.string,
  }).isRequired,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

JustificatifsPersonnalises.defaultProps = {};

const mapStateToProps = state => ({
  numeroAllocataire: state.demarches.numeroAllocataire,
  codePostal: state.demarches.codePostal,
  restaurantScolaireInfo: state.demarches.restaurantScolaireInfo,
  tokenInfo: state.connection.tokenInfo,
  enfantsInfo: state.demarches.enfantsInfo,
  adresses: state.demarches.adresses,
});

export default connect(mapStateToProps)(enhancer(JustificatifsPersonnalises));
