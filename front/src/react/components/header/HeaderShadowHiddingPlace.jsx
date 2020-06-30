import React from 'react';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

const useStyles = makeStyles(() => ({
  root: {
    background: 'white',
    width: '100%',
    height: '10px',
    position: 'sticky',
    top: '-10px',
  },
}));

// Le but de ce composant est de cacher l'ombre du header quand on ne scroll pas.
// Uniquement en version mobile
const HeaderShadowHiddingPlace = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  if (!xsDown || !props.isVisible) {
    return null;
  }

  return <div className={classes.root} />;
};

HeaderShadowHiddingPlace.propTypes = {
  isVisible: PropTypes.bool,
};

HeaderShadowHiddingPlace.defaultProps = {
  isVisible: false,
};

const mapStateToProps = state => ({
  isVisible:
    state.connection.showHeader && state.session.authenticated && !state.navigation.embedded,
});

export default connect(mapStateToProps, null)(enhancer(HeaderShadowHiddingPlace));
