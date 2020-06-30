import { all } from 'redux-saga/effects';
import identitySaga from './identity/sagas';
import demarchesSaga from './demarches/sagas';
import connectionSaga from './connection/sagas';

/**
 * Notice how we now only export the rootSaga
 * single entry point to start all Sagas at once.
 */
function* rootSaga() {
  yield all([identitySaga(), demarchesSaga(), connectionSaga()]);
}

export default rootSaga;
