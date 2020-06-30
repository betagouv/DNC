import { CardContent, Grid } from '@material-ui/core';
import { compose, pure } from 'recompose';

import Button from 'react/components/buttons/button/Button';
import Card from 'react/components/card/Card';
import PropTypes from 'prop-types';
import React from 'react';
import colors from 'style/config.variables.scss';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  cardContent: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    height: '100%',
    padding: '24px',
  },
  grid: {
    padding: '1rem',
    margin: 0,
    height: '100%',
  },
  gridItem: { maxWidth: '40.625rem' },
  button: {
    width: '80%',
  },
  card: {
    [theme.breakpoints.down('xs')]: {
      boxShadow: 'none',
    },
  },
}));

const Split = (props) => {
  const classes = useStyles();

  React.useEffect(() => {
    props.hideHeader();
  }, []);

  return (
    <>
      <Grid container justify="center" alignItems="center" className={classes.grid}>
        <Grid item xs className={classes.gridItem}>
          <Card className={classes.card}>
            <CardContent className={classes.cardContent}>
              <Button
                color={colors.darkishBlue}
                className={classes.button}
                style={{ marginBottom: '30px' }}
                onClick={() => {
                  props.history.push('/home');
                }}
              >
                Accéder au Dossier Numérique du Citoyen
              </Button>
              <Button
                color={colors.darkishBlue}
                className={classes.button}
                style={{ marginBottom: '30px' }}
                onClick={() => {
                  window.location.href
                    = 'http://mairie.dnc.cloudapps.dfp.ovh/demarche/restaurantscolaire.html';
                }}
              >
                Accéder à la démo de la démarche restauration scolaire
              </Button>
              <Button
                color={colors.darkishBlue}
                className={classes.button}
                style={{ marginBottom: '30px' }}
                onClick={() => {
                  window.location.href
                    = 'http://mairie.dnc.cloudapps.dfp.ovh/demarche/cartestationnement.html';
                }}
              >
                Accéder à la démo de la démarche carte de stationnement
              </Button>
              {/* <Button
                color={colors.darkishBlue}
                className={classes.button}
                onClick={() => {
                  props.history.push('/test_justificatifs');
                }}
              >
                Accéder au test des justificatifs
              </Button> */}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </>
  );
};

Split.propTypes = {
  hideHeader: PropTypes.func.isRequired,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

Split.defaultProps = {};

const mapStateToProps = () => ({});

const mapDispatchToProps = dispatch => ({
  hideHeader: () => dispatch({
    type: 'SHOW_HEADER',
    showHeader: false,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(Split));
