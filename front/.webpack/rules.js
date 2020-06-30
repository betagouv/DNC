const { endsWith } = require('lodash');

const {
  babelLoader,
  eslintLoader,
  makeCssLoader,
  makeLessLoader,
  makeSassLoader,
  sassVariablesLoader,
  assetsLoader,
  svgReactLoader,
  htmlLoader,
} = require('./loaders');

// Transpile using babel.
const babelRule = {
  test: /\.jsx?$/i,
  exclude: /(node_modules)/i,
  use: babelLoader,
};

// Eslint.
const eslintRule = {
  test: /\.jsx?$/i,
  // Make sure that they are linted BEFORE they are transformed by babel.
  enforce: 'pre',
  // Do not lint files in node_modules.
  exclude: /(node_modules)/i,
  use: eslintLoader,
};

const makeCssRule = ({ mode } = {}) => ({
  test: file => !endsWith(file, '.module.css') && endsWith(file, '.css'),
  use: makeCssLoader({ mode }),
});

const makeCssModulesRule = ({ mode } = {}) => ({
  test: /\.module\.css$/i,
  use: makeCssLoader({ mode, enableModules: true }),
});

const makeLessRule = ({ mode } = {}) => ({
  test: /\.less$/,
  use: makeLessLoader({ mode }),
});

const makeSassRule = ({ mode } = {}) => ({
  test: file =>
    !endsWith(file, '.module.scss') &&
    !endsWith(file, '.variables.scss') &&
    endsWith(file, '.scss'),
  use: makeSassLoader({ mode }),
});

const makeSassModulesRule = ({ mode } = {}) => ({
  test: /\.module\.scss$/i,
  use: makeSassLoader({ mode, enableModules: true }),
});

const sassVariablesRule = {
  test: /\.variables\.scss$/i,
  use: sassVariablesLoader,
};

// Rule for assets file and NODE_MODULES svg.
const assetsRule = {
  test: /(node_modules[/\\].+\.svg)|(\.(jpg|jpeg|bmp|png|gif|eot|otf|ttf|woff|woff2|ico|pdf))$/i,
  use: assetsLoader,
};

const htmlRule = {
  test: /\.(html)$/i,
  use: htmlLoader,
};
// Rule for app svg files.
const svgReactRule = {
  test: /\.svg$/i,
  exclude: /node_modules/i,
  use: svgReactLoader,
};

module.exports = {
  babelRule,
  eslintRule,
  makeCssRule,
  makeCssModulesRule,
  makeLessRule,
  makeSassRule,
  makeSassModulesRule,
  sassVariablesRule,
  assetsRule,
  svgReactRule,
  htmlRule,
};
