import { makeStyles } from '@material-ui/core/styles';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { compose, pure } from 'recompose';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const useStyles = makeStyles({
  root: props => ({
    display: 'flex',
    flexDirection: 'column',
    height: '100%',
    borderRadius: '20px',
    border: `${props.dashed ? 'dashed' : 'solid'} 2px #e3e9f3`,
    flexGrow: '1',
    padding: '1rem 1.5rem',
    justifyContent: 'center',
  }),
});

const JustificatifsPersonnalisesBox = (props) => {
  const classes = useStyles(props);

  return (
    <div className={classes.root} style={props.style}>
      {props.children}
    </div>
  );
};

JustificatifsPersonnalisesBox.propTypes = {
  style: PropTypes.objectOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  children: PropTypes.node,
};

JustificatifsPersonnalisesBox.defaultProps = {
  style: {},
  children: null,
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
  connect(mapStateToProps, mapDispatchToProps)(enhancer(JustificatifsPersonnalisesBox)),
);
