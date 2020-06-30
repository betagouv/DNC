/**
 * Returns runtime environment variable if set.
 * Runtime environment is configured in `src/index.html.ejs` inside window.env.
 * They are configured using placeholders as in window.env = { A : '$MY_APP_A' }.
 *
 * @param {string} variableName - The variable name.
 * @returns {string} - The variable value.
 */
function getEnvironmentVariable(variableName) {
  if (
    window.env?.[variableName]
    && window.env[variableName] !== `$${process.env.ENV_PREFIX}${variableName}`
  ) {
    return window.env[variableName];
  }

  return null;
}

export default {
  app: {
    version: process.env.npm_package_version,
  },

  whatever: getEnvironmentVariable('WHATEVER'),
};
