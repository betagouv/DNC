import React from 'react';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import colors from 'style/config.variables.scss';
import Stepper from 'react/components/stepper/Stepper';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  title: {
    fontSize: '2.1875rem',
    fontWeight: 'bold',
    lineHeight: '3rem',
    textAlign: 'center',
    color: colors.darkSlateBlue,
  },
}));

const GenericDemarcheHeader = (props) => {
  const classes = useStyles();

  return (
    <>
      <p className={classes.title}>{props.title}</p>

      <Stepper activeStep={props.activeStep} allSteps={props.allSteps} />
    </>
  );
};

GenericDemarcheHeader.propTypes = {
  activeStep: PropTypes.number.isRequired,
  allSteps: PropTypes.arrayOf(PropTypes.string).isRequired,
  title: PropTypes.string.isRequired,
};

GenericDemarcheHeader.defaultProps = {};

export default enhancer(GenericDemarcheHeader);
