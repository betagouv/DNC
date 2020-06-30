import React from 'react';
import { compose, pure } from 'recompose';
import { connect } from 'react-redux';
import { sessionService } from 'redux-react-session';
import PropTypes from 'prop-types';
import Authentication from './Authentication';

const enhancer = compose(pure);

/**
 * Récupère les scopes nécessaires à la démarche en cours.
 *
 * @param {string} codeDemarche - Le code de la démarche.
 * @param {Function} fetchScopes - Fonction redux.
 */
async function fetchCurrentDemarcheScopes(codeDemarche, fetchScopes) {
  const { tokenInfo } = await sessionService.loadSession();

  if (!tokenInfo) {
    return;
  }

  fetchScopes(codeDemarche, tokenInfo.access_token);
}

/**
 * Composant permettant de lancer la cinamtique
 * d'authentification Embedded avec demande de certains scopes.
 *
 * @param {*} props - Les props du composant.
 * @returns {React.Component} - Le composant à afficher.
 */
const EmbeddedAuthenticationSopes = (props) => {
  // Au premier affichage on récupère les scopes pour la démarche en cours
  React.useEffect(() => {
    fetchCurrentDemarcheScopes(props.codeDemarche, props.fetchCurrentDemarcheScopes);
  }, []);

  // Tant qu'on a pas récupéré les scopes, on n'affiche rien
  if (!props.scopes) {
    return null;
  }

  /*
    Une fois les scopes récupérés on lance la cinématique d'authentification
    grâce au composant Authentication
  */
  return (
    <Authentication
      redirectQueryParams={{
        redirect_path: props.redirectPath,
        embedded: true,
        embedded_with_scopes: true,
      }}
      scopes={props.scopes}
    />
  );
};

EmbeddedAuthenticationSopes.propTypes = {
  codeDemarche: PropTypes.string.isRequired,
  fetchCurrentDemarcheScopes: PropTypes.func.isRequired,
  scopes: PropTypes.arrayOf(PropTypes.string),
  redirectPath: PropTypes.string,
};

EmbeddedAuthenticationSopes.defaultProps = {
  scopes: undefined,
  redirectPath: '',
};

const mapStateToProps = state => ({
  scopes: state.demarches.currentDemarcheScopes,
});

const mapDispatchToProps = dispatch => ({
  fetchCurrentDemarcheScopes: (codeDemarche, token) => dispatch({
    type: 'FETCH_CURRENT_DEMARCHE_SCOPES',
    codeDemarche,
    token,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(EmbeddedAuthenticationSopes));
