import { compose, pure } from 'recompose';
import { makeStyles, useTheme } from '@material-ui/core/styles';

import Logger from 'utils/Logger';
import PropTypes from 'prop-types';
import React from 'react';
import authenticationManager from 'services/AuthenticationManager';
import colors from 'style/config.variables.scss';
import { connect } from 'react-redux';
import { identitePivotShape } from 'models/shapes/IdentitePivotShape';
import logoutSvg from 'assets/images/ic-deconnexion-blue.svg';
import menuSvg from 'assets/images/ic-menu.svg';
import { sessionService } from 'redux-react-session';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import userSvg from 'assets/images/ic-usager-blue.svg';
import { withRouter } from 'react-router-dom';
import HeaderItem from './HeaderItem';
import HeaderBellIcon from './HeaderBellIcon';

const enhancer = compose(pure);

const useStyles = makeStyles(theme => ({
  root: {
    height: '5rem',
    position: 'relative',
    marginRight: 'calc(20px + 1rem - 8px)',
    marginLeft: 'calc(20px + 1rem - 8px)',
    paddingTop: '1rem',
    paddingBottom: '1rem',
    [theme.breakpoints.down('xs')]: {
      margin: 0,
      padding: 0,
      boxShadow: 'rgba(0, 0, 0, 0.15) 0px 4px 5px 0px',
    },
  },
  navLink: {
    textDecoration: 'initial',
  },
  icon: {
    width: '1.375rem',
    color: colors.darkishBlue,
    [theme.breakpoints.down('xs')]: {
      width: '2rem',
    },
  },
}));

/**
 * Déconnecte l'utilisateur.
 */
async function logout() {
  try {
    const { tokenInfo } = await sessionService.loadSession();
    authenticationManager.logout(tokenInfo.id_token);
  } catch (err) {
    Logger.error(err);
  }
}

/**
 * Créer un objet contenant les informations d'affichage d'un bouton en fonction des paramètres.
 *
 * @param {string} key - La key du bouton.
 * @param {string} label - Le libellé du bouton.
 * @param {*} icon - L'icon du bouton.
 * @param {Function} onClick - La callback onClick du bouton.
 * @returns {*} - Un object décrivant bouton.
 */
function createButton(key, label, icon, onClick) {
  return {
    key,
    icon,
    label,
    onClick,
  };
}

const Header = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  const xsDown = useMediaQuery(theme.breakpoints.down('xs'));
  const smDown = useMediaQuery(theme.breakpoints.down('sm'));

  if (!props.isVisible) {
    return null;
  }

  let usager = '';

  if (props.identitePivot) {
    usager = `${props.identitePivot.prenoms} ${props.identitePivot.nomFamille}`;
  }

  const buttons = [];

  /* if (xsDown) {
    buttons.push(
      createButton('menu', '', <MenuIcon className={classes.icon} />, () => {
        props.toggleMobileDrawer(true);
      }),
    );
  } */

  buttons.push(
    createButton(
      'profil',
      usager,
      <img src={userSvg} className={classes.icon} alt="Profil" />,
      () => {
        props.history.push('/profil');
      },
    ),
    createButton(
      'alertes',
      'Mes alertes',
      <HeaderBellIcon className={classes.icon} alertNumber={props.alertNumber} alt="Alertes" />,
      () => {
        props.history.push('/demarchesAlerte');
      },
    ),
    createButton(
      'déconnexion',
      'Déconnexion',
      <img src={logoutSvg} className={classes.icon} alt="Déconnexion" />,
      logout,
    ),
  );

  return (
    <>
      <div id="header" className={classes.root}>
        {smDown && (
          <HeaderItem
            key="menu"
            style={{ position: 'absolute', top: 0, bottom: 0 }}
            icon={
              <img src={menuSvg} className={classes.icon} alt="Menu" style={{ width: '2rem' }} />
            }
            onClick={() => {
              props.toggleMobileDrawer(true);
            }}
          />
        )}
        <div
          style={{
            display: 'flex',
            flexDirection: 'row',
            justifyContent: xsDown ? 'center' : 'flex-end',
            width: '100%',
            height: '100%',
          }}
        >
          {buttons.map(({
            key, label, icon, onClick,
          }) => (
            <HeaderItem key={key} icon={icon} onClick={onClick}>
              {label}
            </HeaderItem>
          ))}
        </div>
        {/* <div
          style={{
            width: '100%',
            position: 'sticky',
            height: '10px',
            top: '0',
            boxShadow: 'rgba(0, 0, 0, 0.15) 0px 4px 20px 0px',
            backgroundColor: 'white',
          }}
        /> */}
      </div>
    </>
  );
};

Header.propTypes = {
  isVisible: PropTypes.bool,
  identitePivot: identitePivotShape.isRequired,
  alertNumber: PropTypes.number,
  toggleMobileDrawer: PropTypes.func.isRequired,
  history: PropTypes.shape({ push: PropTypes.func.isRequired }).isRequired,
};

Header.defaultProps = {
  isVisible: false,
  alertNumber: 2,
};

const mapStateToProps = (state) => {
  let identitePivot = null;

  if (state.identity && state.identity && state.identity && state.identity.identitePivot) {
    identitePivot = state.identity.identitePivot;
  }

  return {
    isVisible:
      state.connection.showHeader && state.session.authenticated && !state.navigation.embedded,
    identitePivot,
  };
};

const mapDispatchToProps = dispatch => ({
  toggleMobileDrawer: mobileDrawerVisible => dispatch({
    type: 'TOGGLE_MOBILE_DRAWER',
    mobileDrawerVisible,
  }),
});

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(enhancer(Header)));
