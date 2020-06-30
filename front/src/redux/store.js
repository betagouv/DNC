import {
  createStore, applyMiddleware, combineReducers, compose,
} from 'redux';
import createSagaMiddleware from 'redux-saga';

import thunk from 'redux-thunk';
import { createLogger } from 'redux-logger';
import { responsiveStoreEnhancer } from 'redux-responsive';
import { composeWithDevTools } from 'redux-devtools-extension';
import { sessionReducer } from 'redux-react-session';
import { persistStore, persistReducer } from 'redux-persist';
import storageSession from 'redux-persist/lib/storage/session';
import responsiveReducers from './responsive/reducers';
import navigationReducers from './navigation/reducers';
import identityReducers from './identity/reducers';
import connectionReducers from './connection/reducers';
import demarchesReducers from './demarches/reducers';
import justificatifsReducers from './justificatifs/reducers';

import rootSaga from './sagas';

// Do not refactor this into !['production', 'test'].includes(process.env.NODE_ENV)
// Because uglify cannot statically analyze this.
const isDevEnvironment = process.env.NODE_ENV !== 'production' && process.env.NODE_ENV !== 'test';

// Create root reducer.
const reducers = combineReducers({
  responsive: responsiveReducers,
  navigation: navigationReducers,
  identity: identityReducers,
  session: sessionReducer,
  connection: connectionReducers,
  demarches: demarchesReducers,
  justificatifs: justificatifsReducers,
});
const persistConfig = {
  key: 'root',
  storage: storageSession,
  blacklist: ['embedded'],
};
const persistedReducer = persistReducer(persistConfig, reducers);

const sagaMiddleware = createSagaMiddleware();

const middlewares = [thunk, sagaMiddleware];

if (isDevEnvironment) {
  middlewares.push(
    createLogger({
      collapsed: true,
      diff: true,
      duration: true,
    }),
  );
}

const composer = isDevEnvironment ? composeWithDevTools : compose;

const enhancers = composer(responsiveStoreEnhancer, applyMiddleware(...middlewares));

const initialState = {};

const store = createStore(persistedReducer, initialState, enhancers);
const persistor = persistStore(store);

sagaMiddleware.run(rootSaga);

export { store, persistor };
