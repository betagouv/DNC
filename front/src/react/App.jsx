import React from 'react';
import { Provider as ReduxProvider } from 'react-redux';
import jss from 'jss';

import { store, persistor } from 'redux/store';
import { sessionService } from 'redux-react-session';

import { PersistGate } from 'redux-persist/integration/react';
import { StylesProvider } from '@material-ui/core/styles';
import preset from 'jss-preset-default';
import RootContainer from './RootContainer';

jss.setup(preset());

/**
 * App root.
 *
 * @returns {object} JSX.
 */
export default function App() {
  sessionService.initSessionService(store, {
    redirectPath: '/',
    driver: 'COOKIES',
    expires: null, // 0.0208333, // 30 minutes
  });

  return (
    <ReduxProvider store={store}>
      <PersistGate loading={null} persistor={persistor}>
        <StylesProvider jss={jss}>
          <RootContainer />
        </StylesProvider>
      </PersistGate>
    </ReduxProvider>
  );
}
