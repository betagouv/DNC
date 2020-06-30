import { call, put, takeLatest } from 'redux-saga/effects';
import usagerManager from 'services/UsagerManager';

/**
 * Worker Saga: will be fired on FETCH_IDENTITE_PIVOT actions.
 *
 * @param {any} action - The action.
 */
function* fetchIdentitePivot(action) {
  try {
    const identitePivot = yield call(usagerManager.fetchIdentitePivot, action.token);

    yield put({ type: 'FETCH_IDENTITE_PIVOT_SUCCEEDED', identitePivot });
  } catch (e) {
    yield put({ type: 'FETCH_IDENTITE_PIVOT_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_INFOS_FAMILLE actions.
 *
 * @param {any} action - The action.
 */
function* fetchInfosFamille(action) {
  try {
    const infosFamille = yield call(
      usagerManager.fetchInfosFamille,
      action.numeroAllocataire,
      action.codePostal,
      action.token,
    );

    yield put({
      type: 'FETCH_INFOS_FAMILLE_SUCCEEDED',
      infosFamille,
      numeroAllocataire: action.numeroAllocataire,
      codePostal: action.codePostal,
    });
  } catch (e) {
    yield put({ type: 'FETCH_INFOS_FAMILLE_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_INFOS_FISCALES actions.
 *
 * @param {any} action - The action.
 */
function* fetchInfosFiscales(action) {
  try {
    const infosFiscales = yield call(
      usagerManager.fetchInfosFiscales,
      action.numeroFiscal,
      action.referenceAvisFiscal,
      action.token,
    );

    yield put({ type: 'FETCH_INFOS_FISCALES_SUCCEEDED', infosFiscales });
  } catch (e) {
    yield put({ type: 'FETCH_INFOS_FISCALES_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_INFOS_SECU actions.
 *
 * @param {any} action - The action.
 */
function* fetchInfosSecu(action) {
  try {
    const infosSecu = yield call(usagerManager.fetchInfosSecu, action.token);

    yield put({ type: 'FETCH_INFOS_SECU_SUCCEEDED', infosSecu });
  } catch (e) {
    yield put({ type: 'FETCH_INFOS_SECU_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_INFOS_POLE_EMPLOI actions.
 *
 * @param {any} action - The action.
 */
function* fetchInfosPoleEmploi(action) {
  try {
    const infosPoleEmploi = yield call(usagerManager.fetchInfosPoleEmploi, action.token);

    yield put({ type: 'FETCH_INFOS_POLE_EMPLOI_SUCCEEDED', infosPoleEmploi });
  } catch (e) {
    yield put({ type: 'FFETCH_INFOS_POLE_EMPLOI_FAILED', message: e.message });
  }
}

/**
 * Worker Saga: will be fired on FETCH_INFOS_DEMARCHES actions.
 *
 * @param {any} action - The action.
 */
function* fetchInfosDemarches(action) {
  try {
    const infosDemarches = yield call(usagerManager.fetchDemarchesInfos, action.token);

    yield put({ type: 'FETCH_INFOS_DEMARCHES_SUCCEEDED', infosDemarches });
  } catch (e) {
    yield put({ type: 'FETCH_INFOS_DEMARCHES_FAILED', message: e.message });
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
  yield takeLatest('FETCH_IDENTITE_PIVOT', fetchIdentitePivot);
  yield takeLatest('FETCH_INFOS_FAMILLE', fetchInfosFamille);
  yield takeLatest('FETCH_INFOS_FISCALES', fetchInfosFiscales);
  yield takeLatest('FETCH_INFOS_SECU', fetchInfosSecu);
  yield takeLatest('FETCH_INFOS_POLE_EMPLOI', fetchInfosPoleEmploi);
  yield takeLatest('FETCH_INFOS_DEMARCHES', fetchInfosDemarches);
}

export default identitySaga;
