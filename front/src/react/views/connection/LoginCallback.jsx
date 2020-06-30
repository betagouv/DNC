import { compose, pure } from 'recompose';

import DemarcheManager from 'services/DemarcheManager';
import Logger from 'utils/Logger';
import PropTypes from 'prop-types';
import React from 'react';
import { Redirect } from 'react-router-dom';
import { connect } from 'react-redux';
import qs from 'qs';

const enhancer = compose(pure);

const LoginCallback = (props) => {
  const [redirect, setRedirect] = React.useState(false);
  const [path, setPath] = React.useState('/informations');
  const [embeddedWithScopes, setEmbeddedWithScopes] = React.useState(false);

  React.useEffect(() => {
    // eslint-disable-next-line jsdoc/require-jsdoc
    async function fetchData() {
      try {
        props.hideHeader();

        const {
          // le code d'authorization OpenID
          code,
          // boolean indiquant si on est dans le workflow embedded
          embedded,
          // l'URI du FS vers laquelle rediriger l'usager une fois le workflow embedded terminé
          redirect_uri: fsRedirectUri,
          // le path de la page vers laquelle rediriger l'usager sur le DNC après l'authentification
          redirect_path: redirectPath,
          // boolean indiquant que le token a été généré avec les scopes (écran de consentement)
          embedded_with_scopes: embeddeWithScopes,
          embedded_data: embeddedData,
        } = qs.parse(props.location.search, {
          ignoreQueryPrefix: true,
        });

        setEmbeddedWithScopes(embeddeWithScopes === 'true');

        const redirectQueryParams = {
          redirect_path: redirectPath,
          redirect_uri: fsRedirectUri,
          embedded_data: embeddedData,
          embedded,
          embedded_with_scopes: embeddeWithScopes,
        };

        if (!code) {
          return;
        }

        props.fetchToken(code, redirectQueryParams);
        if (redirectQueryParams.siretPartenaire && redirectQueryParams.embedded) {
          DemarcheManager.fetchUserSession(
            redirectQueryParams.codeDemarche,
            'token', redirectQueryParams.siretPartenaire,
          );
        } else {
          DemarcheManager.fetchUserSession(redirectQueryParams.codeDemarche, 'token');
        }

        if (embedded && fsRedirectUri && fsRedirectUri !== '') {
          props.setFsRedirectUri(fsRedirectUri);
        }

        if (embedded && embeddedData && JSON.parse(embeddedData)) {
          props.setEmbeddedData(JSON.parse(embeddedData));
        }

        if (redirectPath) {
          setPath(redirectPath);
        }

        setRedirect(true);
      } catch (error) {
        Logger.error(error);
      }
    }

    fetchData();
  }, []);

  if (redirect && props.tokenInfo && props.tokenInfo.expirationTimestamp > Date.now()) {
    return (
      <Redirect
        to={{
          pathname: path,
          state: {
            embeddedWithScopes,
          },
        }}
      />
    );
  }

  return null;
};

LoginCallback.propTypes = {
  location: PropTypes.shape({
    search: PropTypes.string.isRequired,
  }).isRequired,
  hideHeader: PropTypes.func.isRequired,
  setFsRedirectUri: PropTypes.func.isRequired,
  fetchToken: PropTypes.func.isRequired,
  tokenInfo: PropTypes.objectOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number]))
    .isRequired,
  setEmbeddedData: PropTypes.func.isRequired,
};

const mapStateToProps = state => ({
  tokenInfo: state.connection.tokenInfo,
});

const mapDispatchToProps = dispatch => ({
  fetchToken: (authorizationCode, redirectQueryParams) => dispatch({
    type: 'FETCH_TOKEN',
    authorizationCode,
    redirectQueryParams,
  }),
  hideHeader: () => dispatch({
    type: 'SHOW_HEADER',
    showHeader: false,
  }),
  setFsRedirectUri: fsRedirectUri => dispatch({
    type: 'SET_FS_REDIRECT_URI',
    fsRedirectUri,
  }),
  setEmbeddedData: embeddedData => dispatch({
    type: 'SET_EMBEDDED_DATA',
    embeddedData,
  }),
});

export default connect(mapStateToProps, mapDispatchToProps)(enhancer(LoginCallback));
