import { call, put, takeLatest } from 'redux-saga/effects';
import demarcheManager from 'services/DemarcheManager';

/**
 * Worker Saga: will be fired on FETCH_MULTI_ACCUEIL actions.
 *
 * @param {any} action - The action.
 */
function* fetchCarteStationnementInfo(action) {
  try {
    const carteStationnementInfo = yield call(
      demarcheManager.fetchCarteStationnementUsagerInfo,
      action.props,
      action.vehiculeSociete,
      action.token,
    );

    yield put({ type: 'FETCH_CARTE_STATIONNEMENT_SUCCEEDED', carteStationnementInfo });
  } catch (e) {
    yield put({ type: 'FETCH_CARTE_STATIONNEMENT_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_ADRESSES actions.
 *
 * @param {any} action - The action.
 */
function* fetchVehiculesInfo(action) {
  try {
    const vehiculesInfo = yield call(
      demarcheManager.fetchVehiculesUsagerInfo,
      action.idDemarche,
      action.token,
    );

    yield put({ type: 'FETCH_VEHICULES_SUCCEEDED', vehiculesInfo });
  } catch (e) {
    yield put({ type: 'FETCH_VEHICULES_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_ADRESSES actions.
 *
 * @param {any} action - The action.
 */
function* fetchSocietesInfo(action) {
  try {
    const societesInfo = yield call(
      demarcheManager.fetchSocietesUsagerInfo,
      action.idDemarche,
      action.token,
    );

    yield put({ type: 'FETCH_SOCIETES_SUCCEEDED', societesInfo });
  } catch (e) {
    yield put({ type: 'FETCH_SOCIETES_FAILED', message: e.message });
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
function* carteStationnementSaga() {
  yield takeLatest('FETCH_CARTE_STATIONNEMENT', fetchCarteStationnementInfo);
  yield takeLatest('FETCH_VEHICULES', fetchVehiculesInfo);
  yield takeLatest('FETCH_SOCIETES', fetchSocietesInfo);
}

export default carteStationnementSaga;
