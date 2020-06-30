import {
  all,
  call,
  put,
  takeLatest,
} from 'redux-saga/effects';

import demarcheManager from 'services/DemarcheManager';
import carteStationnementSaga from './cartestationnement/sagas';

/**
 * Worker Saga: will be fired on FETCH_CURRENT_DEMARCHE_SCOPES actions.
 *
 * @param {any} action - The action.
 */
function* fetchCurrentDemarcheScopes(action) {
  try {
    const currentDemarcheScopes = yield call(
      demarcheManager.fetchScopes,
      action.codeDemarche,
      action.token,
    );

    yield put({ type: 'FETCH_CURRENT_DEMARCHE_SCOPES_SUCCEEDED', currentDemarcheScopes });
  } catch (e) {
    yield put({ type: 'FETCH_CURRENT_DEMARCHE_SCOPES_FAILED', message: e.message });
  }
}
/**
 * Worker Saga: will be fired on FETCH_ABONNEMENT_STATIONNEMENT actions.
 *
 * @param {any} action - The action.
 */
function* fetchStationnementInfo(action) {
  try {
    const stationnementUsagerInfo = yield call(
      demarcheManager.fetchStationnementUsagerInfo,
      action.idDemarche,
      action.token,
    );

    yield put({ type: 'FETCH_ABONNEMENT_STATIONNEMENT_SUCCEEDED', stationnementUsagerInfo });
  } catch (e) {
    yield put({ type: 'FETCH_ABONNEMENT_STATIONNEMENT_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_MULTI_ACCUEIL actions.
 *
 * @param {any} action - The action.
 */
function* fetchRestaurantScolaireInfo(action) {
  try {
    const restaurantScolaireInfo = yield call(
      demarcheManager.fetchRestaurantScolaireUsagerInfo,
      action.numeroAllocataire,
      action.codePostal,
      action.token,
    );

    yield put({ type: 'FETCH_RESTAURANT_SCOLAIRE_SUCCEEDED', restaurantScolaireInfo });
  } catch (e) {
    yield put({ type: 'FETCH_RESTAURANT_SCOLAIRE_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_ENFANTS actions.
 *
 * @param {any} action - The action.
 */
function* fetchEnfantsInfo(action) {
  try {
    const enfantsInfo = yield call(
      demarcheManager.fetchEnfantsUsagerInfo,
      action.idDemarche,
      action.token,
    );

    yield put({ type: 'FETCH_ENFANTS_SUCCEEDED', enfantsInfo });
  } catch (e) {
    yield put({ type: 'FETCH_ENFANTS_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_ADRESSES actions.
 *
 * @param {any} action - The action.
 */
function* fetchAdressesInfo(action) {
  try {
    const adresses = yield call(
      demarcheManager.fetchAdressesUsagerInfo,
      action.idDemarche,
      action.token,
    );

    yield put({ type: 'FETCH_ADRESSES_SUCCEEDED', adresses });
  } catch (e) {
    yield put({ type: 'FETCH_ADRESSES_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_ADRESSES actions.
 *
 * @param {any} action - The action.
 */
function* fetchSocieteAdressesInfo(action) {
  try {
    const societeAdresses = yield call(
      demarcheManager.fetchSocieteAdressesUsagerInfo,
      action.idDemarche,
      action.token,
      action.siret,
    );

    yield put({ type: 'FETCH_SOCIETE_ADRESSES_SUCCEEDED', societeAdresses });
  } catch (e) {
    yield put({ type: 'FETCH_SOCIETE_ADRESSES_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_USER_SESSION actions.
 *
 * @param {any} action - The action.
 */
function* fetchUserSession(action) {
  try {
    const userSession = yield call(
      demarcheManager.fetchUserSession,
      action.token,
      action.idPivot,
    );

    yield put({ type: 'FETCH_USER_SESSION_SUCCEEDED', userSession });
  } catch (e) {
    yield put({ type: 'FETCH_USER_SESSION_FAILED', message: e.message });
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
function* demarcheSaga() {
  yield all([
    carteStationnementSaga(),
    takeLatest('FETCH_CURRENT_DEMARCHE_SCOPES', fetchCurrentDemarcheScopes),
    takeLatest('FETCH_ABONNEMENT_STATIONNEMENT', fetchStationnementInfo),
    takeLatest('FETCH_RESTAURANT_SCOLAIRE', fetchRestaurantScolaireInfo),
    takeLatest('FETCH_ENFANTS', fetchEnfantsInfo),
    takeLatest('FETCH_ADRESSES', fetchAdressesInfo),
    takeLatest('FETCH_SOCIETE_ADRESSES', fetchSocieteAdressesInfo),
    takeLatest('FETCH_USER_SESSION', fetchUserSession),
  ]);
}

export default demarcheSaga;
