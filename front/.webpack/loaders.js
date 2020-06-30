const path = require('path');

const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const AntdScssThemePlugin = require('antd-scss-theme-plugin');
const pixrem = require('pixrem');
const autoprefixer = require('autoprefixer');

const babelLoader = [
  {
    loader: 'babel-loader',
    options: {
      cacheDirectory: false,
    },
  },
];

const eslintLoader = [
  {
    loader: 'eslint-loader',
    options: {
      // Use root .eslintrc to get the config.
      configFile: path.resolve(__dirname, '../.eslintrc.js'),
      // Do not stop the build on a lint error.
      emitWarning: true,
    },
  },
];

const makeCssLoader = ({ mode = 'development', enableModules = false, importLoaders = 0 } = {}) => [
  {
    loader:
      mode === 'production'
        ? // In production, extract css to an external stylesheet to allow caching.
          MiniCssExtractPlugin.loader
        : // In development, use the style-loader (faster and allows MHR).
          'style-loader',
  },
  {
    loader: 'css-loader',
    options: {
      sourceMap: true,
      importLoaders: importLoaders + 1,
      modules: enableModules
        ? {
            localIdentName:
              mode === 'production'
                ? // In production, only set the hash of the class name.
                  '[hash:base64:8]'
                : // In development, add the actual class name to the hash to make it easier to debug.
                  '[local]--[hash:base64:8]',
          }
        : undefined,
    },
  },
  {
    loader: 'postcss-loader',
    options: {
      sourceMap: true,
      plugins: [pixrem(), autoprefixer],
    },
  },
];

const makeLessLoader = ({ mode, importLoaders = 0 } = {}) => [
  ...makeCssLoader({
    mode,
    importLoaders: importLoaders + 1,
  }),
  AntdScssThemePlugin.themify({
    loader: 'less-loader',
    options: {
      javascriptEnabled: true,
    },
  }),
];

const makeSassLoader = ({ mode, enableModules, importLoaders = 0 } = {}) => [
  ...makeCssLoader({
    enableModules,
    mode,
    importLoaders: importLoaders + 2,
  }),
  {
    // This loader will resolve URLs relative to the source file.
    // (otherwise they will be relative to the built destination file and URLs will not work).
    loader: 'resolve-url-loader',
    options: {
      sourceMap: true,
    },
  },
  AntdScssThemePlugin.themify({
    loader: 'sass-loader',
    options: {
      sourceMap: true,
      includePaths: [
        path.resolve(__dirname, '../src/'),
        path.resolve(__dirname, '../node_modules/'),
      ],
    },
  }),
];

const sassVariablesLoader = [
  {
    loader: 'sass-extract-loader',
    options: {
      plugins: ['sass-extract-js'],
      includePaths: [
        path.resolve(__dirname, '../src/'),
        path.resolve(__dirname, '../node_modules/'),
      ],
    },
  },
];

const assetsLoader = [
  {
    loader: 'file-loader',
    options: {
      name: 'static/[name].[hash:8].[ext]',
    },
  },
];

const htmlLoader = [
  {
    loader: 'html-loader',
  },
];

const svgReactLoader = [
  {
    loader: 'svg-url-loader',
  },
  {
    loader: 'image-webpack-loader',
    options: {
      // Optimize svg files.
      svgo: {
        plugins: [
          // Keep the viewbox and remove the hardcoded dimensions
          // to be able to set the dimensions via css.
          { removeViewBox: false },
          { removeDimensions: true },

          // Removing ids breaks some svgs.
          { cleanupIDs: false },
        ],
      },
    },
  },
];

module.exports = {
  babelLoader,
  eslintLoader,
  makeCssLoader,
  makeLessLoader,
  makeSassLoader,
  sassVariablesLoader,
  assetsLoader,
  svgReactLoader,
  htmlLoader,
};
