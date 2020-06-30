import { Drawer, Hidden, List } from '@material-ui/core';
import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import { NavLink } from 'react-router-dom';
import PropTypes from 'prop-types';
import React from 'react';
import aideSvg from 'assets/images/ic-aide-blue.svg';
import annuaireSvg from 'assets/images/ic-annuaire-blue.svg';
import chat from 'assets/images/chat.svg';
import { connect } from 'react-redux';
import demarcheSvg from 'assets/images/ic-demarche-blue.svg';
// import historiqueSvg from 'assets/images/ic-historique-blue.svg';
// import indisponibleImg from 'assets/images/ic-indisponible.png';
import informationSvg from 'assets/images/ic-informations-blue.svg';
import justificatifSvg from 'assets/images/ic-justificatif-blue_2.svg';
import ResponsiveDrawerItem from './ResponsiveDrawerItem';

const enhancer = compose(pure);

const drawerWidth = '7.8125rem';
const mobileDrawerWidth = '125px';

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
    [theme.breakpoints.down('sm')]: {
      width: 'initial',
      flexShrink: 'initial',
    },
  },
  drawerPaper: {
    border: 0,
    display: 'flex',
    justifyContent: 'center',
  },
  list: {
    width: drawerWidth,
    [theme.breakpoints.down('sm')]: {
      width: mobileDrawerWidth,
    },
  },
  navLink: {
    textDecoration: 'initial',
  },
}));

/**
 * Créer un objet contenant les informations d'affichage d'un bouton en fonction des paramètres.
 *
 * @param {string} key - La key du bouton.
 * @param {string} label - Le libellé du bouton.
 * @param {*} icon - L'icon du bouton.
 * @param {string} navLinkPath - Le path de redirection du bouton.
 * @returns {*} - Un object décrivant bouton.
 */
function createButtonData(key, label, icon, navLinkPath) {
  return {
    key,
    label,
    icon,
    navLinkPath,
  };
}

const ResponsiveDrawer = (props) => {
  const classes = useStyles();
  const theme = useTheme();

  if (!props.isVisible) {
    return null;
  }

  const visibleButtons = [
    createButtonData('informations', 'Mes informations', informationSvg, '/informations'),
    createButtonData('demarches', 'Mes démarches en cours', demarcheSvg, '/demarches'),
    createButtonData('justificatifs', 'Mes justificatifs', justificatifSvg, '/justificatifs'),
    createButtonData('annuaire', 'Annuaire services FranceConnect', annuaireSvg, '/annuaire'),
    // createButtonData('historique', 'Mon historique', historiqueSvg, '/historique'),
    createButtonData('aide', 'Se faire accompagner', chat, '/en_developpement'),
    createButtonData('aide', 'Aide', aideSvg, '/en_developpement'),
  ];

  const closeMobileDrawer = () => {
    props.toggleMobileDrawer(false);
  };

  const drawer = (
    <div>
      <List className={classes.list}>
        {visibleButtons.map(({
          key, label, icon, navLinkPath,
        }, index) => {
          const drawerItem = (
            <ResponsiveDrawerItem
              key={!navLinkPath ? key : null}
              label={label}
              svg={icon}
              index={index}
              onClick={() => {
                // On ferme le drawer mobile lorsqu'on clique sur un des boutons du drawer
                if (props.mobileDrawerVisible) {
                  props.toggleMobileDrawer(false);
                }
              }}
            />
          );

          if (navLinkPath) {
            return (
              <NavLink key={key} to={{ pathname: navLinkPath }} className={classes.navLink}>
                {drawerItem}
              </NavLink>
            );
          }

          return drawerItem;
        })}
      </List>
    </div>
  );

  return (
    <div className={classes.root}>
      <nav className={classes.drawer} aria-label="mailbox folders">
        <Hidden mdUp implementation="js">
          <Drawer
            variant="temporary"
            anchor={theme.direction === 'rtl' ? 'right' : 'left'}
            open={props.mobileDrawerVisible}
            onClose={closeMobileDrawer}
            classes={{
              paper: classes.drawerPaper,
            }}
            ModalProps={{
              keepMounted: true, // Better open performance on mobile.
            }}
          >
            {drawer}
          </Drawer>
        </Hidden>
        <Hidden smDown implementation="js">
          <Drawer
            classes={{
              paper: classes.drawerPaper,
            }}
            variant="permanent"
            open
          >
            {drawer}
          </Drawer>
        </Hidden>
      </nav>
    </div>
  );
};

ResponsiveDrawer.propTypes = {
  isVisible: PropTypes.bool,
  toggleMobileDrawer: PropTypes.func.isRequired,
  mobileDrawerVisible: PropTypes.bool,
};

ResponsiveDrawer.defaultProps = {
  isVisible: false,
  mobileDrawerVisible: false,
};

const mapStateToProps = state => ({
  isVisible:
    state.connection.showHeader && state.session.authenticated && !state.navigation.embedded,
  mobileDrawerVisible: state.navigation.mobileDrawerVisible,
});

const mapDispatchToProps = dispatch => ({
  toggleMobileDrawer: mobileDrawerVisible => dispatch({
    type: 'TOGGLE_MOBILE_DRAWER',
    mobileDrawerVisible,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(ResponsiveDrawer));
