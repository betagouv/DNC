import { compose, pure } from 'recompose';

import Authentication from 'react/components/authentication/Authentication';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import qs from 'qs';

const enhancer = compose(pure);

/**
 * Composant pour démarrer un workflow embedded.
 *
 * @param {*} props - Les props du composant.
 * @returns {*} - Le composant permettant de lancer la cinématique d'authentification.
 */
const Embedded = (props) => {
  /*
    redirect_path le path vers lequel redirigé l'usager après l'authentification FC
    codeDemarche le code de la démarche (ex: restaurantscolaire)
    fsRedirectUri l'URI vers laquelle rediriger l'usager à la fin de la cinématique embedded
    embbed indique qu'on entre dans une cinématique embedded
  */

  const { code_demarche: codeDemarche, redirect_uri: fsRedirectUri, data: embeddedData } = qs.parse(
    props.location.search,
    {
      ignoreQueryPrefix: true,
    },
  );

  let redirectPath = '';

  switch (codeDemarche) {
    case 'CARTE_STATIONNEMENT':
      redirectPath = '/cartestationnement';
      break;
    case 'cartestationnement':
      redirectPath = '/cartestationnement';
      break;
    case 'RESTAURATION_SCOLAIRE':
      redirectPath = '/restaurantscolaire';
      break;
    case 'restaurantscolaire':
      redirectPath = '/restaurantscolaire';
      break;
    default:
      redirectPath = 'erreur';
  }

  const redirectQueryParams = {
    redirect_path: redirectPath,
    redirect_uri: fsRedirectUri,
    embedded_data: embeddedData,
    embedded: true,
  };

  // Lance la cinématique d'authentification avec les scopes par défaut
  return <Authentication redirectQueryParams={redirectQueryParams} />;
};

Embedded.propTypes = {
  location: PropTypes.shape({
    search: PropTypes.string.isRequired,
  }).isRequired,
};

Embedded.defaultProps = {};

const mapDispatchToProps = dispatch => ({
  initialState: () => dispatch({ type: 'INITIAL_STATE' }),
});

export default connect(null, mapDispatchToProps)(enhancer(Embedded));
