import { CardContent, Container } from '@material-ui/core';
import { compose, pure } from 'recompose';

import Authentication from 'react/components/authentication/Authentication';
import Card from 'react/components/card/Card';
import FranceConnectButton from 'react/components/buttons/fc/FranceConnectButton';
import PropTypes from 'prop-types';
import React from 'react';
import { Redirect } from 'react-router-dom';
import colors from 'style/config.variables.scss';
import { connect } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import styles from './Home.module.scss';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  cardContent: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: '1rem',
  },
  grid: {
    padding: '1rem',
    width: '100%',
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
  },
  card: {
    minWidth: '300px',
    width: '50%',
    maxWidth: '430px',
    height: 'initial',
    [theme.breakpoints.down('xs')]: {
      boxShadow: 'none',
    },
  },
  fcInfos: {
    all: 'unset',
    color: colors.darkishBlue,
    cursor: 'pointer',
    '&:hover': {
      fontWeight: 'bold',
    },
  },
}));

const Home = (props) => {
  const classes = useStyles();

  const [startAuthentication, setStartAuthentication] = React.useState(false);

  const scopes = ['openid', 'identite_pivot', 'address', 'droits_assurance_maladie'];

  if (props.authenticated) {
    return (
      <Redirect
        to={{
          pathname: '/informations',
        }}
      />
    );
  }

  if (startAuthentication) {
    return <Authentication scopes={scopes} />;
  }

  return (
    <>
      <Container className={classes.grid}>
        <Card className={classes.card}>
          <CardContent className={classes.cardContent}>
            <p className={styles.cardTitle}>Dossier Numérique du Citoyen</p>

            <p className={styles.cardInfo}>
            Le Dossier Numérique du Citoyen vous permet d’accéder à vos données personnelles,
             connues de l’administration. Il ne conserve pas vos informations.
            Merci de vous connecter avec{' '}
              <b>FranceConnect</b>
            </p>
            <FranceConnectButton
              className={styles.fcButton}
              onClick={() => {
                setStartAuthentication(true);
              }}
            />

            <button
              className={classes.fcInfos}
              onClick={() => {
                window.open('https://franceconnect.gouv.fr');
              }}
            >
              <u>&gt; En savoir plus sur FranceConnect</u>
            </button>
          </CardContent>
        </Card>
      </Container>
    </>
  );
};

Home.propTypes = {
  authenticated: PropTypes.bool,
};

Home.defaultProps = {
  authenticated: false,
};

const mapStateToProps = state => ({
  isHeaderVisible: state.connection.showHeader,
  authenticated: state.session.authenticated,
});

const mapDispatchToProps = dispatch => ({
  showHeader: () => dispatch({
    type: 'SHOW_HEADER',
    showHeader: true,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(Home));
