import { Route, BrowserRouter as Router, Switch } from 'react-router-dom';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import Header from 'react/components/header/Header';
import HeaderShadowHiddingPlace from 'react/components/header/HeaderShadowHiddingPlace';
import Home from 'react/views/home/Home';
import Loadable from 'react-loadable';
import LoginCallback from 'react/views/connection/LoginCallback';
import LogoutCallback from 'react/views/connection/LogoutCallback';
import PrivateRoute from 'react/components/privateRoute/PrivateRoute';
import PropTypes from 'prop-types';
import React from 'react';
import ResponsiveDrawer from 'react/components/drawer/ResponsiveDrawer';
import { connect } from 'react-redux';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import styles from './RootContainer.module.scss';

const AsyncIndisponible = Loadable({
  loader: () => import('./pages/IndisponiblePage'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncInformations = Loadable({
  loader: () => import('./pages/InformationsPage'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncProfil = Loadable({
  loader: () => import('./pages/ProfilPage'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncSplit = Loadable({
  loader: () => import('./Split'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncDemarches = Loadable({
  loader: () => import('./pages/DemarchesPage'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncDemarchesAlerte = Loadable({
  loader: () => import('./pages/DemarchesPageAlerte'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncTestsJustificatifs = Loadable({
  loader: () => import('./TestsJustificatifs'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncAbonnementStationnement = Loadable({
  loader: () => import('./views/embedded/stationnement/AbonnementStationnement'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncRestaurantScolaire = Loadable({
  loader: () => import('./views/embedded/restaurantscolaire/RestaurantScolaire'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncCarteStationnement = Loadable({
  loader: () => import('./views/embedded/cartestationnement/CarteStationnement'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncEmbedded = Loadable({
  loader: () => import('./views/embedded/Embedded'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncVerifJustificatif = Loadable({
  loader: () => import('./VerifJustificatif'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncJustificatifs = Loadable({
  loader: () => import('./pages/JustificatifsPage'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncJustificatifsPersonnalises = Loadable({
  loader: () => import('./views/justificatifs/personnalises/JustificatifsPersonnalises'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncHistorique = Loadable({
  loader: () => import('./pages/HistoriquePage'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const AsyncAnnuaire = Loadable({
  loader: () => import('./pages/AnnuairePage'),
  // eslint-disable-next-line jsdoc/require-jsdoc
  loading() {
    return null;
  },
});

const useStyles = makeStyles(theme => ({
  page: {
    display: 'flex',
    width: '100%',
    flexDirection: 'column',
    height: '100vh',
  },
  contentRoot: {
    display: 'flex',
    width: '100%',
    flexDirection: 'column',
    height: '100%',
    [theme.breakpoints.down('xs')]: {
      overflow: 'auto',
    },
  },
  content: {
    display: 'flex',
    width: '100%',
    flexDirection: 'column',
    height: '100%',
    [theme.breakpoints.down('xs')]: {
      height: 'calc(100% - 10px)',
    },
  },
}));

const enhancer = compose(pure);

const RootContainer = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));

  return (
    <>
      <div
        id="app_content"
        className={styles.appContent}
        style={{ background: xsDown ? 'white' : null }}
      >
        <Router>
          <ResponsiveDrawer />
          <div id="page" className={classes.page}>
            <Header alertNumber={props.nbAlertes} />

            <div id="content_root" className={classes.contentRoot}>
              <HeaderShadowHiddingPlace />

              <div id="content" className={classes.content}>
                <Switch>
                  <Route path="/home" exact component={Home} />
                  <Route path="/" exact component={AsyncSplit} />

                  <PrivateRoute
                    path="/en_developpement"
                    exact
                    component={AsyncIndisponible}
                    authenticated={props.authenticated}
                  />

                  <PrivateRoute
                    path="/informations"
                    exact
                    component={AsyncInformations}
                    authenticated={props.authenticated}
                    drawerIndex={0}
                  />

                  <PrivateRoute
                    path="/profil"
                    exact
                    component={AsyncProfil}
                    authenticated={props.authenticated}
                  />

                  <PrivateRoute
                    path="/demarches"
                    exact
                    component={AsyncDemarches}
                    authenticated={props.authenticated}
                    drawerIndex={1}
                  />

                  <PrivateRoute
                    path="/demarchesAlerte"
                    exact
                    component={AsyncDemarchesAlerte}
                    authenticated={props.authenticated}
                    // drawerIndex={1}
                  />

                  <PrivateRoute
                    path={['/justificatifs/personnalises', '/justificatifs/personnalises/:step']}
                    exact
                    component={AsyncJustificatifsPersonnalises}
                    authenticated={props.authenticated}
                    drawerIndex={2}
                  />

                  <PrivateRoute
                    path="/justificatifs"
                    component={AsyncJustificatifs}
                    authenticated={props.authenticated}
                    drawerIndex={2}
                  />

                  <PrivateRoute
                    path="/annuaire"
                    exact
                    component={AsyncAnnuaire}
                    authenticated={props.authenticated}
                    drawerIndex={3}
                  />
                  <PrivateRoute
                    path="/historique"
                    exact
                    component={AsyncHistorique}
                    authenticated={props.authenticated}
                    drawerIndex={4}
                  />

                  <PrivateRoute
                    path={['/stationnement', '/stationnement/:step']}
                    exact
                    component={AsyncAbonnementStationnement}
                    authenticated={props.authenticated}
                    demarche
                  />

                  <PrivateRoute
                    path={['/restaurantscolaire', '/restaurantscolaire/:step']}
                    exact
                    component={AsyncRestaurantScolaire}
                    authenticated={props.authenticated}
                    demarche
                  />

                  <PrivateRoute
                    path={['/cartestationnement', '/cartestationnement/:step']}
                    exact
                    component={AsyncCarteStationnement}
                    authenticated={props.authenticated}
                    demarche
                  />

                  <Route path="/test_justificatifs" exact component={AsyncTestsJustificatifs} />

                  <Route path="/verif-justificatif" exact component={AsyncVerifJustificatif} />

                  <Route
                    path="/embedded"
                    exact
                    component={AsyncEmbedded}
                    authenticated={props.authenticated}
                    demarche
                  />
                  <Route path="/logout-callback" exact component={LogoutCallback} />
                  <Route path="/login-callback" exact component={LoginCallback} />
                </Switch>
              </div>
            </div>
          </div>
        </Router>
      </div>
    </>
  );
};

RootContainer.propTypes = {
  authenticated: PropTypes.bool,
  nbAlertes: PropTypes.number,
};

RootContainer.defaultProps = {
  authenticated: false,
  nbAlertes: 2,
};

const mapStateToProps = state => ({
  authenticated: state.session.authenticated,
  nbAlertes: state.alertes,
});

export default connect(mapStateToProps)(enhancer(RootContainer));
