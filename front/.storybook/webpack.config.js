const { antdScssThemePlugin } = require('../.webpack/plugins');

const {
  makeCssRule,
  makeCssModulesRule,
  makeLessRule,
  makeSassRule,
  makeSassModulesRule,
  sassVariablesRule,
  assetsRule,
  svgReactRule,
} = require('../.webpack/rules');

module.exports = ({ config }) => {
  // Default config does not implicitly resolve .jsx files.
  config.resolve.extensions.push('.jsx');

  config.devServer = { stats: 'minimal' };

  // Default config only transforms .js files.
  config.module.rules[0].test = /\.jsx?$/i;

  config.module.rules = [
    // JS/JSX.
    config.module.rules[0],
    // Raw loader.
    config.module.rules[1],
    // Files (mp3 etc.).
    config.module.rules[4],

    makeCssRule(),
    makeCssModulesRule(),
    makeLessRule(),
    makeSassRule(),
    makeSassModulesRule(),
    sassVariablesRule,

    assetsRule,
    svgReactRule,
  ];

  config.plugins.push(antdScssThemePlugin);

  return config;
};
