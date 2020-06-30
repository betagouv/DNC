import React from 'react';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import colors from 'style/config.variables.scss';
import rightArrowSvg from 'assets/images/ic-right-arrow.svg';
import {
  Stepper as MuiStepper, Step, StepLabel, useMediaQuery,
} from '@material-ui/core';
import PropTypes from 'prop-types';
import { withRouter } from 'react-router-dom';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  rootIcon: {
    color: `${colors.lightGreyBlue} !important`,
    width: '1.875rem',
    height: 'auto',
  },
  completedIcon: {
    extend: 'rootIcon',
    color: `${colors.leaf} !important`,
  },
  activeIcon: {
    extend: 'rootIcon',
    color: `${colors.lightNavyBlue} !important`,
  },
  completed: {
    color: `${colors.leaf} !important`,
    fontStyle: 'italic',
  },
  rootLabel: {
    color: `${colors.lightGreyBlue} !important`,
  },
  activeLabel: {
    color: `${colors.lightNavyBlue} !important`,
  },
  stepper: {
    padding: '0 0 1rem 0',
    '& img': {
      width: '1.25rem',
      height: '0.9375rem',
      margin: '0.625rem',
    },
    justifyContent: 'center',
  },
}));

const Stepper = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  return (
    <div
      style={{
        padding: '1rem',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <MuiStepper
        activeStep={props.activeStep}
        className={classes.stepper}
        connector={<img alt="Flèche droite" src={rightArrowSvg} />}
        style={props.style}
      >
        {props.allSteps.map(({ title, path }, index) => (
          <Step
            key={title}
            classes={{ completed: classes.completed }}
            onClick={() => {
              if (index >= props.activeStep) {
                return;
              }
              props.history.push(path);
            }}
          >
            <StepLabel
              // onClick={props.history.push(path)}
              StepIconProps={{
                classes: {
                  root: classes.rootIcon,
                  completed: classes.completed,
                  active: classes.activeIcon,
                },
              }}
              classes={{
                completed: classes.completed,
                root: classes.rootLabel,
                active: classes.activeLabel,
                label: classes.stepLabel,
              }}
            >
              {!xsDown && (
                <b
                  style={{
                    textTransform: 'uppercase',
                    // textDecoration: props.activeStep > index ? 'underline' : '',
                    cursor: props.activeStep > index ? 'pointer' : 'initial',
                  }}
                >
                  {title}
                </b>
              )}
            </StepLabel>
          </Step>
        ))}
      </MuiStepper>

      {xsDown && (
        <b style={{ textTransform: 'uppercase', color: colors.lightNavyBlue, fontSize: '0.8rem' }}>
          {props.allSteps[props.activeStep].title}
        </b>
      )}
    </div>
  );
};

Stepper.propTypes = {
  activeStep: PropTypes.number,
  allSteps: PropTypes.arrayOf(PropTypes.string),
  style: PropTypes.objectOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

Stepper.defaultProps = {
  style: {},
  activeStep: 0,
  allSteps: [],
};

export default withRouter(enhancer(Stepper));
