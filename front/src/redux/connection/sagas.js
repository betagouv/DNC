import { call, put, takeLatest } from 'redux-saga/effects';

import authenticationManager from 'services/AuthenticationManager';

/**
 * Worker Saga: will be fired on FETCH_TOKEN actions.
 *
 * @param {any} action - The action.
 */
function* fetchToken(action) {
  try {
    const tokenInfo = yield call(
      authenticationManager.fetchToken,
      action.authorizationCode,
      action.redirectQueryParams,
    );

    yield put({ type: 'FETCH_TOKEN_SUCCEEDED', tokenInfo, authenticated: true });
  } catch (e) {
    yield put({ type: 'FETCH_TOKEN_FAILED', message: e.message });
  }
}

/*
  Alternatively you may use takeLatest.

  Does not allow concurrent fetches of user. If "FETCH_IDENTITE_PIVOT" gets
  dispatched while a fetch is already pending, that pending fetch is cancelled
  and only the latest one will be run.
*/
/**
 *
 */
function* identitySaga() {
  yield takeLatest('FETCH_TOKEN', fetchToken);
}

/**
 * Mise à jour des alertes après avoir visité la page une fois.
 */
export function* resetAlertes() {
  console.log('update alerte');
  yield put({ type: 'RESET_ALERTES' });
}

export default identitySaga;
