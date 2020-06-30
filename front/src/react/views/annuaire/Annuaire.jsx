import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import { Container } from '@material-ui/core';
import Card from 'react/components/card/Card';
import PropTypes from 'prop-types';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import useMediaQuery from '@material-ui/core/useMediaQuery';

const enhancer = compose(pure);

const useStyles = makeStyles({
  gridContainer: {
    padding: '1.5rem',
    maxWidth: '100%',
    height: '100%',
  },
  iframe: {
    width: '100%',
    height: '100%',
    border: 'none',
    padding: '1.8rem',
  },
  card: {
    height: '100%',
  },
});

const Annuaire = (props) => {
  const theme = useTheme();
  const xsScreen = useMediaQuery(theme.breakpoints.down('xs'));
  const classes = useStyles();

  React.useEffect(() => {
    props.updateSelectedDrawerItem(props.drawerIndex);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const iframe = (
    <iframe
      title="Annuaire FranceConnect"
      sandbox="allow-scripts allow-popups"
      src="https://franceconnect.gouv.fr/nos-services"
      className={classes.iframe}
    />
  );

  if (xsScreen) {
    return iframe;
  }

  return (
    <>
      <Container className={classes.gridContainer}>
        <Card className={classes.card}>{iframe}</Card>
      </Container>
    </>
  );
};

Annuaire.propTypes = {
  drawerIndex: PropTypes.number.isRequired,
  updateSelectedDrawerItem: PropTypes.func.isRequired,
};

Annuaire.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  updateSelectedDrawerItem: selectedKey => dispatch({
    type: 'UPDATE_SELECTED_DRAWER_ITEM',
    selectedKey,
  }),
});

export default connect(null, mapDispatchToProps)(enhancer(Annuaire));
