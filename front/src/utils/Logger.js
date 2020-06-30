/* eslint-disable no-console */

const logger = {
  /**
   * Log debug.
   *
   * @param {string} message - Message to log.
   */
  debug(message) {
    console.debug(message);
  },

  /**
   * Log info.
   *
   * @param {string} message - Message to log.
   */
  info(message) {
    console.info(message);
  },

  /**
   * Log error.
   *
   * @param {string} message - Message to log.
   */
  error(message) {
    console.error(message);
  },

  /**
   * Log warning.
   *
   * @param {string} message - Message to log.
   */
  warning(message) {
    console.warn(message);
  },
};

export default logger;
