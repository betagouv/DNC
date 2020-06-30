import { transform } from 'lodash';
import { createResponsiveStateReducer } from 'redux-responsive';

import { BREAKPOINTS_MAP } from 'config/style';

export default createResponsiveStateReducer(
  { ...BREAKPOINTS_MAP },
  {
    // Add extra fields to the state to make it easier to use.
    extraFields: responsiveState => ({
      // greaterThanOrEqual is built by transforming greaterThan
      greaterThanOrEqual: transform(responsiveState.greaterThan, (result, value, mediaType) => {
        // and combining the value with the `is` field
        result[mediaType] = value || responsiveState.is[mediaType];
      }),

      // lessThanOrEqual is built by transforming greaterThan
      lessThanOrEqual: transform(responsiveState.lessThan, (result, value, mediaType) => {
        // and combining the value with the `is` field
        result[mediaType] = value || responsiveState.is[mediaType];
      }),
    }),
  },
);
