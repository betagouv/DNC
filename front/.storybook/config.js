import React from 'react';
import { transform } from 'lodash';
import { Provider as StoreProvider } from 'react-redux';
import { configure, addDecorator, addParameters } from '@storybook/react';
import { withKnobs } from '@storybook/addon-knobs';
import { INITIAL_VIEWPORTS } from '@storybook/addon-viewport';

import { BREAKPOINTS_MAP } from '../src/config/style';
import { store } from '../src/redux/store';

import '../src/style/main.scss';
import './storybook.scss';

// Add breakpoints viewports to the list of devices.
addParameters({
  viewport: {
    defaultViewport: 'responsive',

    viewports: {
      // Default is responsive (behaves as if no viewport selected).
      responsive: {
        type: 'desktop',
        name: 'Responsive',
        styles: {
          width: '100%',
          height: '100%',
          margin: 0,
          border: 0,
        },
      },

      // Add default devices viewports.
      ...INITIAL_VIEWPORTS,

      // Add app breakpoints as desktop viewports.
      ...transform(BREAKPOINTS_MAP, (result, bpMaxWidth, bpName) => {
        result[bpName] = {
          type: 'desktop',
          name: `${bpName} (${bpMaxWidth}px) -- custom breakpoint`,
          styles: {
            width: `${bpMaxWidth}px`,
            // Otherwise addon-viewport does not fill the device height.
            height: '100%',
            margin: 0,
          },
        };
      }),
    },
  },
});

// Add a redux store for each stories.
addDecorator(getStory => (
  // eslint-disable-next-line react/jsx-filename-extension
  <StoreProvider store={store}>{getStory()}</StoreProvider>
));

// Add knobs.
addDecorator(withKnobs);

/**
 * Load stories.
 */
function loadStories() {
  const req = require.context('../src/react', true, /\.stories\.jsx?$/);
  req.keys().forEach((filename) => {
    req(filename);
  });
}

configure(loadStories, module);
