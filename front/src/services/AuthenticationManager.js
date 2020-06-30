import { sessionService } from 'redux-react-session';
import stringUtils from 'utils/StringUtils';
import qs from 'qs';
import http from './http';

/**
 * Redirige l'utilisateur vers l'endpoint /api/v1/authorize de FranceConnect
afin d'engager la cinématique d'authentification.
 *
 * @param {*} redirectQueryParams - Les paramètres de rédirection
 * une fois l'authentification réussie.
 * @param {string[]} fcSopes - Les différents scopes d'accès.
 */
function doAuthorizeRedirection(
  redirectQueryParams = {},
  fcSopes = ['openid', 'identite_pivot', 'address'],
) {
  /* if (process.env.USE_MOCKS) {
    window.location.href =
      `${window.location.origin}/login-callback?code=11ed11b4-39e0-41e1-9a4c-21b84adc940e&` +
      'state=I4fdjZqFOQDbyqxPFf8KlFcSC7U6k2iFtG7uBaQqCGFxFUsT';
    return;
  } */

  const fcAuthorizeEndpoint = process.env.FC_URI + process.env.FC_AUTHORIZE_ENDPOINT;
  const fcClientId = process.env.FC_CLIENT_ID;

  const fcRedirectUri = encodeURIComponent(
    `${window.location.origin}${process.env.FC_LOGIN_CALLBACK}${qs.stringify(redirectQueryParams, {
      addQueryPrefix: true,
    })}`,
  );

  const fcState = stringUtils.generateRandomString(48);
  const fcNonce = stringUtils.generateRandomString(48);

  const authorizeUrl = `${fcAuthorizeEndpoint}?response_type=code&client_id=${fcClientId}&redirect_uri=${fcRedirectUri}&scope=${fcSopes.join(
    ' ',
  )}&state=${fcState}&nonce=${fcNonce}&acr_values=eidas1`;

  window.location.href = authorizeUrl;
}

/**
 * Effectue une requète HTTP (POST) avec le code d'authorisation FranceConnect,
 * afin de récupérer un token d'accès FranceConnect.
 *
 * @param {string} fcAuthorizationCode - Code d'authorization FranceConnect récupérer
 * après doAuthorizeRedirection ou doAuthorizeRedirectionPopup.
 *
 * @param {any} redirectQueryParams - Les paramètres de redirection.
 *
 * @returns {Promise<any>} Promesse.
 */
async function fetchToken(fcAuthorizationCode, redirectQueryParams = {}) {
  /* if (process.env.USE_MOCKS) {
    const body = {
      expires_in: 60,
      token_info: 'abcd',
      token: 'efgh',
    };

    const expiresIn = body.expires_in;
    const expirationTimestamp = Date.now() + expiresIn * 1000;

    sessionService.saveSession({ tokenInfo: { ...body, expirationTimestamp } });
    return;
  } */

  const fcTokenEndpoint = process.env.FC_URI + process.env.FC_TOKEN_ENDPOINT;

  const fcRedirectUri = encodeURIComponent(
    `${window.location.origin}${process.env.FC_LOGIN_CALLBACK}${qs.stringify(redirectQueryParams, {
      addQueryPrefix: true,
    })}`,
  );
  const fcClientId = process.env.FC_CLIENT_ID;
  const fcClientSecret = process.env.FC_CLIENT_SECRET;

  return new Promise((resolve, reject) => {
    http
      .post(`${window.location.protocol}//${process.env.CORS_BYPASS}/${fcTokenEndpoint}`)
      .type('form')
      .send('grant_type=authorization_code')
      .send(`redirect_uri=${fcRedirectUri}`)
      .send(`client_id=${fcClientId}`)
      .send(`client_secret=${fcClientSecret}`)
      .send(`code=${fcAuthorizationCode}`)
      .end(async (error, res) => {
        if (!res || !res.body || !res.body.access_token) {
          reject(res);
          return;
        }

        const expiresIn = res.body.expires_in;
        const expirationTimestamp = Date.now() + expiresIn * 1000;

        const tokenInfo = {
          ...res.body,
          expirationTimestamp,
        };

        sessionService.saveSession({
          tokenInfo,
        });

        resolve(tokenInfo);
      });
  });
}

/**
 * Redirige l'utilisateur vers l'endpoint /api/v1/logout de FranceConnect
 * afin de déconnecter l'utilisateur de FranceConnect.
 *
 * @param {string} tokenId - L'identifiant du token d'accès FranceConnect.
 */
function logout(tokenId) {
  /* if (process.env.USE_MOCKS) {
    window.location.href = `${window.location.origin}/logout-callback`;
    return;
  }
 */
  const fcTokenEndpoint = process.env.FC_URI + process.env.FC_LOGOUT_ENDPOINT;
  const fcRedirectUri = encodeURI(`${window.location.origin}${process.env.FC_LOGOUT_CALLBACK}`);

  window.location.href = `${fcTokenEndpoint}?post_logout_redirect_uri=${fcRedirectUri}&id_token_hint=${tokenId}`;
}

export default {
  doAuthorizeRedirection,
  fetchToken,
  logout,
};
