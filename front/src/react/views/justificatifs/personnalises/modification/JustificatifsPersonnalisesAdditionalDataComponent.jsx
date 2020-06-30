import { makeStyles } from '@material-ui/core/styles';
import React from 'react';
import { compose, pure } from 'recompose';
import colors from 'style/config.variables.scss';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const useStyles = makeStyles({
  container: {
    all: 'unset',
    display: 'flex',
    alignItems: 'center',
    height: '50px',
    borderRadius: '20px',
    backgroundColor: '#ffffff',
    marginBottom: '1.5rem',
    cursor: 'pointer',
    paddingLeft: '0.625rem',
    paddingRight: '0.625rem',

    '&:hover': {
      backgroundColor: '#e3e9f3',
    },

    '& .MuiButton-root': {
      fontSize: '0.875rem',
      fontWeight: 'bold',
      color: colors.darkSlateBlue,
      textTransform: 'initial',
    },

    '& .MuiSvgIcon-root': {
      display: 'none',
    },
  },
  source: {
    marginLeft: 'auto',
    fontStyle: 'italic',
    marginRight: '0.5rem',
    fontSize: '0.875rem',
    color: '#b3b3b3',
  },
});

const JustificatifsPersonnalisesAdditionalDataComponent = (props) => {
  const classes = useStyles();

  return (
    <button className={classes.container} onClick={props.onClick} style={props.style}>
      {props.label}

      {props.children}

      {props.source && props.source !== '' && <p className={classes.source}>{props.source}</p>}

      {props.endButton}
    </button>
  );
};

JustificatifsPersonnalisesAdditionalDataComponent.propTypes = {
  children: PropTypes.node,
  onClick: PropTypes.func,
  endButton: PropTypes.node,
  label: PropTypes.string,
  source: PropTypes.string,
  style: PropTypes.objectOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
};

JustificatifsPersonnalisesAdditionalDataComponent.defaultProps = {
  children: null,
  onClick: () => {},
  endButton: null,
  label: undefined,
  source: undefined,
  style: {},
};

export default enhancer(JustificatifsPersonnalisesAdditionalDataComponent);
