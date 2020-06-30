import { makeStyles } from '@material-ui/core/styles';
import documentSvg from 'assets/images/ic-document.svg';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { compose, pure } from 'recompose';
import colors from 'style/config.variables.scss';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const useStyles = makeStyles({
  button: {
    alignSelf: 'center',
    minWidth: '15.625rem',
    marginTop: '2rem',
    marginBottom: '2rem',
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

    '&:hover': ({ enabled }) => ({
      backgroundColor: enabled ? '#e3e9f3' : '',
    }),

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
});

const JustificatifsPersonnalisesSelectionButton = (props) => {
  const classes = useStyles({ enabled: props.enabled });

  let buttonStyle = {
    border: 'solid 1px rgba(0, 0, 0, 0.12)',
    boxShadow: '',
  };

  let labelColor = 'rgba(0, 0, 0, 0.26)';

  let documentImg = documentSvg.replace(/fill='(%23.\S+)'/g, "fill='%2300000042'");

  if (props.justificatif.enabled) {
    buttonStyle = {
      border:
        props.index === props.justificatifInitial.index ? `solid 1px ${colors.darkishBlue}` : '',
      boxShadow:
        props.index === props.justificatifInitial.index ? '' : '0 2px 15px 0 rgba(0, 0, 0, 0.25)',
    };

    labelColor = props.index === props.justificatifInitial.index ? colors.darkishBlue : '';

    documentImg = documentSvg;
  }

  return (
    <button
      style={buttonStyle}
      className={classes.justificatifCard}
      onClick={() => {
        props.setJustificatifPersonnaliseInitial({ ...props.justificatif, index: props.index });
      }}
    >
      <img id="document_icon" alt="Document" src={documentImg} />
      <p
        style={{
          textOverflow: 'ellipsis',
          overflow: 'hidden',
          whiteSpace: 'nowrap',
          color: labelColor,
          marginRight: '0.625rem',
        }}
      >
        {props.justificatif.label}
      </p>
    </button>
  );
};

JustificatifsPersonnalisesSelectionButton.propTypes = {
  justificatifInitial: PropTypes.shape({
    label: PropTypes.string,
    enabled: PropTypes.bool,
    data: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
    index: PropTypes.number,
  }),
  justificatif: PropTypes.shape({
    label: PropTypes.string,
    enabled: PropTypes.bool,
    data: PropTypes.arrayOf(PropTypes.objectOf(PropTypes.string)),
  }),
  setJustificatifPersonnaliseInitial: PropTypes.func.isRequired,
  index: PropTypes.number,
  enabled: PropTypes.bool,
};

JustificatifsPersonnalisesSelectionButton.defaultProps = {
  justificatifInitial: {},
  justificatif: {},
  index: -1,
  enabled: false,
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
  connect(mapStateToProps, mapDispatchToProps)(enhancer(JustificatifsPersonnalisesSelectionButton)),
);
