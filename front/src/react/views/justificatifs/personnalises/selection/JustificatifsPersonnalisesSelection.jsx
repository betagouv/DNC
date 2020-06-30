import { makeStyles, useTheme } from '@material-ui/core/styles';
import { useMediaQuery } from '@material-ui/core';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import Button from 'react/components/buttons/button/Button';
import FixedFooter from 'react/components/FixedFooter';
import { compose, pure } from 'recompose';
import colors from 'style/config.variables.scss';
import PropTypes from 'prop-types';
import JustificatifsPersonnalisesApercu from '../JustificatifsPersonnalisesApercu';
import JustificatifsPersonnalisesBox from '../JustificatifsPersonnalisesBox';
import JustificatifsPersonnalisesSelectionButton from './JustificatifsPersonnalisesSelectionButton';
import getJustificatifsPersonnalises from './justificatifsPersonnalises';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  button: {
    alignSelf: 'center',
    minWidth: '15.625rem',
    marginTop: '2rem',
    marginBottom: '2rem',

    [theme.breakpoints.down('xs')]: {
      marginTop: '0.5rem',
      marginBottom: '0.5rem',
    },
  },
  justificatifCard: {
    all: 'unset',
    display: 'flex',
    alignItems: 'center',
    width: '100%',
    // maxWidth: '800px',
    height: '50px',
    borderRadius: '20px',
    backgroundColor: '#ffffff',

    marginBottom: '1.5rem',

    '&:hover': {
      backgroundColor: '#e3e9f3',
    },

    '& #document_icon': {
      width: '1.1875rem',
      margin: '0 0.625rem 0 0.625rem',
    },

    '& p': {
      fontSize: '0.9375rem',
      color: '#4f4f4f',
    },

    '& .MuiButton-root': {
      fontSize: '0.875rem',
      fontWeight: 'bold',
      color: colors.darkSlateBlue,
      textTransform: 'initial',
    },
  },
}));

const JustificatifsPersonnalisesSelection = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  const justificatifs = getJustificatifsPersonnalises(props);

  React.useEffect(() => {
    if (props.defaultJustificatif && props.defaultJustificatif !== '') {
      const justificatif = justificatifs
        .map((justif, index) => ({ ...justif, index }))
        .filter(justif => justif.id === props.defaultJustificatif)[0];

      props.setJustificatifPersonnaliseInitial(justificatif);
    } else {
      props.setJustificatifPersonnaliseInitial(undefined);
    }
  }, []);

  let previewComponent = (
    <>
      {Object.keys(props.justificatifInitial).length === 0 && (
        <JustificatifsPersonnalisesBox
          dashed
          style={{
            width: '50%',
            maxWidth: '500px',
            margin: '0 0.5rem',
          }}
        >
          <p
            style={{
              color: '#b3b3b3',
              fontSize: '1rem',
              fontWeight: 'bold',
              textAlign: 'center',
              justifySelf: 'center',
            }}
          >
            Informations affichées sur le justificatif
          </p>
        </JustificatifsPersonnalisesBox>
      )}

      {Object.keys(props.justificatifInitial).length > 0 && (
        <JustificatifsPersonnalisesApercu
          label={props.justificatifInitial.label}
          data={props.justificatifInitial.data}
        />
      )}
    </>
  );

  const isButtonDisabled
    = Object.keys(props.justificatifInitial).length === 0 || !props.justificatifInitial.enabled;

  let validationButton = (
    <Button
      disabled={isButtonDisabled}
      size="small"
      color={colors.darkishBlue}
      className={classes.button}
      onClick={() => {
        props.history.push('modification');
      }}
    >
      Continuer
    </Button>
  );

  if (xsDown) {
    previewComponent = null;

    validationButton = isButtonDisabled ? null : <FixedFooter>{validationButton}</FixedFooter>;
  }

  return (
    <>
      <div
        style={{
          display: 'flex',
          flexDirection: 'row',
          height: !xsDown ? '100%' : '',
          justifyContent: 'space-evenly',
          alignItems: 'center',
        }}
      >
        <JustificatifsPersonnalisesBox
          style={{
            width: '50%',
            maxWidth: '500px',
            borderRadius: xsDown ? 'initial' : '',
            border: xsDown ? 'initial' : '',
            margin: '0 0.5rem',
          }}
        >
          <p
            style={{
              color: '#4f4f4f',
              fontSize: '1rem',
              fontWeight: 'bold',
              textAlign: 'center',
              marginBottom: '1rem',
            }}
          >
            Sélectionner le justificatif souhaité
          </p>

          {justificatifs.map((justificatif, index) => (
            <JustificatifsPersonnalisesSelectionButton index={index} justificatif={justificatif} />
          ))}
        </JustificatifsPersonnalisesBox>

        {previewComponent}
      </div>

      {validationButton}
    </>
  );
};

JustificatifsPersonnalisesSelection.propTypes = {
  justificatifInitial: PropTypes.shape({
    label: PropTypes.string,
    enabled: PropTypes.bool,
    data: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
  }),
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
  defaultJustificatif: PropTypes.string,
  setJustificatifPersonnaliseInitial: PropTypes.func.isRequired,
};

JustificatifsPersonnalisesSelection.defaultProps = {
  justificatifInitial: {},
  defaultJustificatif: undefined,
};

const mapStateToProps = state => ({
  justificatifInitial: state.justificatifs.justificatifInitial,
});

const mapDispatchToProps = dispatch => ({
  setJustificatifPersonnaliseInitial: justificatifInitial => dispatch({
    type: 'SET_JUSTIFICATIF_PERSONNALISE_INITIAL',
    justificatifInitial,
  }),
});

export default withRouter(
  connect(mapStateToProps, mapDispatchToProps)(enhancer(JustificatifsPersonnalisesSelection)),
);
