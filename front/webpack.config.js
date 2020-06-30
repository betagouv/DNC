const path = require('path');

const { pickBy } = require('lodash');
const chalk = require('chalk');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const HtmlReplaceWebpackPlugin = require('html-replace-webpack-plugin');
const ModuleScopePlugin = require('react-dev-utils/ModuleScopePlugin');
const CaseSensitivePathsWebpackPlugin = require('case-sensitive-paths-webpack-plugin');
const WatchMissingNodeModulesPlugin = require('react-dev-utils/WatchMissingNodeModulesPlugin');
const StyleLintPlugin = require('stylelint-webpack-plugin');
const errorOverlayMiddleware = require('react-dev-utils/errorOverlayMiddleware');
const noopServiceWorkerMiddleware = require('react-dev-utils/noopServiceWorkerMiddleware');
const OptimizeCssAssetsWebpackPlugin = require('optimize-css-assets-webpack-plugin');
const TerserWebpackPlugin = require('terser-webpack-plugin');
const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer');
const CompressionPlugin = require('compression-webpack-plugin');
const BrotliPlugin = require('brotli-webpack-plugin'); // brotli

const webpack = require('webpack');

const { antdScssThemePlugin } = require('./.webpack/plugins');

const {
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
} = require('./.webpack/rules');

/**
 * Get all environment variables whose name starts with the ENV_PREFIX (case insensitive).
 *
 * @returns {object} Filtered process.env.
 */
function getAppEnvironment() {
  const prefix = (process.env.ENV_PREFIX || '').toLowerCase();
  return pickBy(process.env, (value, key) => key.toLowerCase().startsWith(prefix));
}

module.exports = ({ generateReport = false } = {}, { mode = 'development' } = {}) => {
  /* eslint-disable no-console */
  console.log(chalk`Building for {cyan.bold ${mode}}`);
  console.log(
    `• Bundle analysis report: ${generateReport ? chalk.green.bold('✔') : chalk.red.bold('Nope')}`,
  );
  /* eslint-enable no-console */

  const appEnv = getAppEnvironment();

  // Served from the root by webpack-dev-server in development and nginx in production.
  const publicPath = '/';

  // Point sourcemap entries to original disk location (format as URL on Windows).
  const devtoolModuleFilenameTemplate = info =>
    path.relative('./src', info.absoluteResourcePath).replace(/\\/g, '/');

  const output =
    mode === 'production'
      ? {
          path: path.join(__dirname, './.dist/'),
          filename: 'js/[name].[contenthash].min.js',
          chunkFilename: 'static/js/[name].[chunkhash:8].chunk.js',
          publicPath,
          devtoolModuleFilenameTemplate,
        }
      : {
          path: path.join(__dirname, './.dev/'),
          filename: 'js/bundle.min.js',
          chunkFilename: 'static/js/[name].chunk.js',
          publicPath,
          devtoolModuleFilenameTemplate,
        };

  const extractCssPlugin = new MiniCssExtractPlugin({
    filename: 'css/[name].[contenthash].min.css',
    ignoreOrder: true,
  });

  const htmlPlugin = new HtmlWebpackPlugin({
    filename: 'index.html',
    template: './src/index.html.ejs',
  });

  const rules = [
    // JavaScript.
    babelRule,
    // Sass, less and css.
    makeCssRule({ mode }),
    makeCssModulesRule({ mode }),
    makeLessRule({ mode }),
    makeSassRule({ mode }),
    makeSassModulesRule({ mode }),
    sassVariablesRule,

    // Assets.
    assetsRule,
    htmlRule,
    // App svg files (specific loader to have the actual <svg> tag in the DOM).
    svgReactRule,
  ];

  if (mode === 'production') {
    rules.push(eslintRule);
  }

  return {
    mode,

    devtool: 'source-map',

    // App entry point.
    entry: [
      mode === 'development' && require.resolve('react-dev-utils/webpackHotDevClient'),
      './src/index.jsx',
    ].filter(Boolean),

    // Bundle output.
    output,

    module: {
      rules,
    },

    resolve: {
      // Resolve absolute imports using these paths (in this order).
      modules: ['./src/', './node_modules/'],

      extensions: ['.json', '.js', '.jsx'],

      plugins: [
        // Prevent importing files outside of src/ (or node_modules/) except package.json.
        new ModuleScopePlugin('./src/', ['./package.json']),
      ],
    },

    plugins: [
      // Extract CSS to an external stylesheet. Better performance in production (allows caching).
      // In development we use style-loader to allow HMR with scss files.
      mode === 'production' && extractCssPlugin,

      // Generate index.html linking to the generated bundles (js and css).
      htmlPlugin,

      // In production, variables are resolved at runtime by Nginx.
      // Replace variables for other environments at build time.
      // This means that you need to restart webpack when editing the environment.
      mode !== 'production' &&
        new HtmlReplaceWebpackPlugin(
          Object.keys(appEnv).map(key => ({
            pattern: `$${key}`,
            replacement: appEnv[key],
          })),
        ),

      // Define specific environment variables in the bundle.
      new webpack.DefinePlugin({
        'process.env': JSON.stringify(process.env),
      }),

      // HMR plugin.
      mode === 'development' && new webpack.HotModuleReplacementPlugin(),

      // Throw error when a required path does not match the case of the actual path.
      new CaseSensitivePathsWebpackPlugin(),

      // Trigger a new build when a node module package is installed.
      mode === 'development' && new WatchMissingNodeModulesPlugin(path.resolve('./node_modules/')),

      // Lint SCSS files.
      new StyleLintPlugin(),

      // Antd sass theme configuration.
      antdScssThemePlugin,

      // If needed, generate a report to analyze why the bundle is large.
      generateReport &&
        new BundleAnalyzerPlugin({
          analyzerMode: 'static',
          reportFilename: path.resolve(
            path.join(__dirname, '.reports/bundle-analyzer-report.html'),
          ),
        }),

      new CompressionPlugin({
        filename: '[path].gz[query]',
        algorithm: 'gzip',
        test: /\.(js|css|html|svg)$/,
        threshold: 8192,
        minRatio: 0.8,
      }),
      new BrotliPlugin({
        // brotli plugin
        asset: '[path].br[query]',
        test: /\.(js|css|html|svg)$/,
        threshold: 10240,
        minRatio: 0.8,
      }),
    ].filter(Boolean),

    // Some libraries import Node modules but don't use them in the browser.
    // Tell Webpack to provide empty mocks for them so importing them works.
    node: {
      dgram: 'empty',
      fs: 'empty',
      net: 'empty',
      tls: 'empty',
      child_process: 'empty',
    },

    optimization: {
      minimizer: [
        new TerserWebpackPlugin({
          cache: true,
          parallel: true,
          sourceMap: true, // set to true if you want JS source maps
        }),
        new OptimizeCssAssetsWebpackPlugin({
          cssProcessorOptions: {
            map: {
              inline: false,
              annotation: true,
            },
          },
        }),
      ],
    },

    // Dev server.
    devServer:
      mode === 'development'
        ? {
            publicPath,

            contentBase: './.dev',
            watchContentBase: true,
            watchOptions: {
              ignored: /node_modules/,
            },
            disableHostCheck: true,

            // Only show errors.
            stats: 'minimal',

            // Enable HMR.
            hot: true,

            // Proxy the requests using the variables used by nginx in production.
            proxy: {
              [process.env.NGINX_PROXY_PATH]: {
                target: process.env.NGINX_PROXY_TARGET_URL,
                pathRewrite: { [`^${process.env.NGINX_PROXY_PATH}`]: '' },
              },
            },

            // Serve index.html on 404.
            historyApiFallback: {
              // Paths with dots should still use the history fallback.
              // See https://github.com/facebookincubator/create-react-app/issues/387.
              disableDotRule: true,
            },

            // Use 8080 port as default.
            // Allow users to override via environment.
            port: +(process.env[`${process.env.ENV_PREFIX}WDS_PORT`] || 8080),

            /**
             * Add middlewares (probably). I copied that from create-react-app.
             *
             * @param {object} app - App.
             */
            before(app) {
              // This lets us open files from the runtime error overlay.
              app.use(errorOverlayMiddleware());
              // This service worker file is effectively a 'no-op' that will reset any
              // previous service worker registered for the same host:port combination.
              // We do this in development to avoid hitting the production cache if
              // it used the same host and port.
              // https://github.com/facebookincubator/create-react-app/issues/2272#issuecomment-302832432
              app.use(noopServiceWorkerMiddleware());
            },
          }
        : undefined,
  };
};
