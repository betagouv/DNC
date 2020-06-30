import { compose, pure } from 'recompose';
import authenticationManager from 'services/AuthenticationManager';
import PropTypes from 'prop-types';

const enhancer = compose(pure);

/**
 * Composant pour lancer la cinématique d'authentification.
 *
 * @param {*} props - Les props du composant.
 * @returns {*} - Null car rien à afficher.
 */
const Authentication = (props) => {
  authenticationManager.doAuthorizeRedirection(props.redirectQueryParams, props.scopes);

  return null;
};

Authentication.propTypes = {
  redirectQueryParams: PropTypes.objectOf(PropTypes.oneOf(PropTypes.string, PropTypes.bool)),
  scopes: PropTypes.PropTypes.arrayOf(PropTypes.string),
};

Authentication.defaultProps = {
  redirectQueryParams: null,
  scopes: undefined,
};

export default enhancer(Authentication);
