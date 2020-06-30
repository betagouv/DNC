import React from 'react';
import { compose, pure } from 'recompose';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import colors from 'style/config.variables.scss';
import styles from './AbonnementStationnement.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles({
  completed: {
    color: `${colors.darkSlateBlue} !important`,
  },
  active: {
    color: `${colors.darkishBlue} !important`,
  },
});

const StationnementHeader = (props) => {
  const classes = useStyles();

  return (
    <>
      <p className={styles.cardTitle}>Je souscris à un abonnement de stationnement</p>

      <Stepper
        activeStep={props.step}
        alternativeLabel
        style={{ width: '100%', marginTop: '20px' }}
      >
        <Step key="Vérifier mes informations" classes={{ completed: classes.completed }}>
          <StepLabel
            StepIconProps={{ classes: { completed: classes.completed, active: classes.active } }}
          >
            <b>Vérifier mes informations</b>
          </StepLabel>
        </Step>
        <Step key={2} classes={{ active: classes.active }}>
          <StepLabel
            StepIconProps={{ classes: { completed: classes.completed, active: classes.active } }}
          >
            <b>Transmettre mes informations</b>
          </StepLabel>
        </Step>
      </Stepper>
    </>
  );
};

StationnementHeader.propTypes = {
  step: PropTypes.number.isRequired,
};

StationnementHeader.defaultProps = {};

const mapStateToProps = () => ({});

export default connect(mapStateToProps)(enhancer(StationnementHeader));
