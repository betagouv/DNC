import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import Card from 'react/components/card/Card';
import { Grid, CardContent } from '@material-ui/core';
import indisponibleImg from 'assets/images/ic-indisponible.png';
import PropTypes from 'prop-types';
import colors from 'style/config.variables.scss';

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

const Indisponible = () => {
  const classes = useStyles();

  return (
    <>
      <Grid container justify="center" alignItems="center" className={classes.grid}>
        <Grid item xs className={classes.gridItem}>
          <Card className={classes.card}>
            <CardContent className={classes.cardContent}>
              <img
                alt="Page en développement"
                src={indisponibleImg}
                style={{ width: '5rem', height: '5rem' }}
              />
              <br />
              <p style={{ color: colors.darkishBlue }}>Cette page est en cours de développement.</p>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </>
  );
};

Indisponible.propTypes = {
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

Indisponible.defaultProps = {};

const mapStateToProps = () => ({});

export default connect(mapStateToProps)(enhancer(Indisponible));
