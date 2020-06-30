import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import Card from 'react/components/card/Card';
import { Container } from '@material-ui/core';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import useMediaQuery from '@material-ui/core/useMediaQuery';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridContainer: {
    padding: '1.5rem',
    width: '100%',
    height: '100%',
    maxWidth: '1000px',
  },
  iframe: {
    width: '100%',
    height: '100%',
    border: 'none',
    padding: '1.8rem',
  },
});

const Historique = (props) => {
  const theme = useTheme();
  const xsScreen = useMediaQuery(theme.breakpoints.down('xs'));
  const classes = useStyles();

  React.useEffect(() => {
    props.updateSelectedDrawerItem(props.drawerIndex);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const iframe = (
    <iframe
      title="Historique FranceConnect"
      sandbox="allow-scripts allow-same-origin"
      src="https://fcp.integ01.dev-franceconnect.fr/traces"
      className={classes.iframe}
    />
  );

  if (xsScreen) {
    return iframe;
  }

  return (
    <Container className={classes.gridContainer}>
      <Card>
        {iframe}
      </Card>
    </Container>
  );
};

Historique.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

Historique.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(Historique));
