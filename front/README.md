# Web-app front-end DNC

A boilerplate for frontend development, using:
- [`webpack@4`](https://github.com/webpack/webpack)
- [`react@16`](https://github.com/facebook/react)
- [`babel@7`](https://github.com/babel/babel)
- [`eslint@5`](https://github.com/eslint/eslint)
- [`jest`](https://github.com/facebook/jest)
- [`storybook@4`](https://github.com/storybooks/storybook)
- [`react-cosmos`](https://github.com/react-cosmos/react-cosmos)
- [`commitlint`](https://github.com/marionebl/commitlint)
- [`stylelint`](https://github.com/stylelint/stylelint)
- [`husky`](https://github.com/typicode/husky)
- [`lint-staged`](https://github.com/okonet/lint-staged)

See [Tools](#tools) for a description of each tool.

## Requirements

1. Install [Node >= 10](https://nodejs.org/en/download/) (ideally install the latest LTS, currently Node 10).
2. Install `yarn`
  ```sh
  $ npm install -g yarn
  ```

## Setup development environment

### Install project dependencies

```sh
# Go to project folder
$ cd /project/path

# Install project dependencies
$ yarn install
```

### Set environment variables

The environment variables in `.env` and `.env.local` files are set before launching the dev server (see below to launch the dev server).

DO NOT set secrets in `.env` file. There should be only generic environment variables (public variables that are common to every developer of the project, such as `NODE_ENV`).

Your secrets must be set in `.env.local`. If you need to override variables in `.env`, just override them in `.env.local`. Consider removing them from `.env`, except if you want to set defaults.

```sh
# Copy the sample file (DBAA: don't do this if you already have a .env.local file)
$ cp .env.locale.example .env.local
```

All variables are prefixed with `MY_APP_`, but you can change this prefix in `.env`.
If you change the prefix in the `.env` file, **DON'T FORGET TO CHANGE IT IN `index.html.ejs`**.

In the bundle, the only variables accessible are the ones you expose in `index.html.ejs` (see the exemple there).

Then replace the environment variables:

| Variable               | Description                                                   | Default value |
| ---------------------- | ------------------------------------------------------------- | ------------- |
| ENV_PREFIX             | Prefix for app specific variables                             | `MY_APP_`     |
| NGINX_PROXY_PATH       | The requests' urls you want to proxy                          | `/api`        |
| NGINX_PROXY_TARGET_URL | The target of the proxy (your backend url)                    |               |
| MY_APP_WDS_PORT        | Port used by `webpack-dev-server` (when using `yarn run dev`) | `8080`        |

## Run development environment

```sh
# Launch webpack dev server with reverse proxy and hot module replacement
$ yarn run dev
```

## Build production package

```sh
$ yarn run build
```

Your bundle is now in `.dist/`.

## Build docker image

```sh
# Build production package
$ yarn run build

# Build docker image
$ docker build \
  --build-arg APP_EXPOSED_PORT=8080 \
  --build-arg NGINX_PROXY_PATH=/api \
  --build-arg NGINX_PROXY_TARGET_URL=http://localhost:3000/ \
  --build-arg NGINX_WEBSOCKET_PROXY_TARGET_URL=http://localhost:3000 \
  --build-arg NGINX_CSP= \
  --build-arg NGINX_SENTRY_REPORT_URI= \
  -t <YOUR_IMAGE_NAME> \
  .
```

## Run production

### Setup environment

Setup environment variables via `.env` files before launching the container.

### Run

Assuming the `.env` and `.env.local` files contain your **production** configuration:
```sh
$ docker run \
  --name <YOUR_CONTAINER_NAME> \
  --env-file .env \
  --env-file .env.local \
  -d \
  <YOUR_IMAGE_NAME>
```

#### Variables d'environnement

* NGINX_PROXY_PATH
* NGINX_PROXY_TARGET_URL
* NGINX_WEBSOCKET_PROXY_TARGET_URL
* NGINX_CSP
* NGINX_SENTRY_REPORT_URI

## Tools

### [Webpack](https://github.com/webpack/webpack)

Webpack is the main tool used for development. It bundles your app and all its dependencies in a build folder (javascript, sass, assets, etc.), transpiles using babel, lints using eslint, etc.

The webpack configuration is in `webpack.config.js`.

### [React](https://github.com/facebook/react)

React is the most popular component-based frontend library.

The boilerplate sets the environment to work with React, but you can use any frontend library. The React-specific tools are `storybook` and `react-cosmos` (you can remove them completely), and you'll need to change the eslint config from `eslint-config-airbnb` to `eslint-config-airbnb-base`. You can also remove the `@babel/preset-react` from `.babelrc`.

### [Babel](https://github.com/babel/babel)

Babel is a transpiler used to transform the latest javascript syntax into plain old javascript (es5).

The babel configuration is in `.babelrc`.

### [Eslint](https://github.com/eslint/eslint)

Eslint is a linter which enforces code consistency accross your app. Configuration is a modified [`airbnb`](https://github.com/airbnb/javascript/tree/master/packages/eslint-config-airbnb) configuration.

Eslint configuration is in `.eslintrc.js`.

### [Jest](https://github.com/facebook/jest)

Jest is a test runner. It is configured to use babel to transpile the code.

Configuration is in `jest.config.js` and setup files are in `.jest/`.

### [Storybook](https://github.com/storybooks/storybook)

Storybook is the most popular tool to work on isolated presentation component. It provides a great interface for plugins. It needs webpack to work.

Configuration can be found in `.storybook/`.

### [react-cosmos](https://github.com/react-cosmos/react-cosmos)

`react-cosmos` is a tool similar to Storybook, but less popular and with less plugins, but with different features (proxies etc.). You can use either or both.

Configuration is in `.cosmos/`.

### [Commitlint](https://github.com/marionebl/commitlint)

Commit is used with `husky` to validate your commit messages when commiting.

Configuration is in `.commitlintrc.js`, and follows the [conventional commits guideline](https://conventionalcommits.org/).

### [Stylelint](https://github.com/stylelint/stylelint)

Stylelint is a linter for Css and SASS/SCSS files. Current configuration is the default one.

Configuration is in `.stylelintrc.js`.

### [Husky](https://github.com/typicode/husky)

Husky is a tool that allows you to easily execute node scripts on git hooks.

Configuration is in `.huskyrc.js`.

### [lint-staged](https://github.com/okonet/lint-staged)

`lint-staged`, via `husky`, lints your staged files and rejects your commit on an error.

## Ant design theme customization
Antd is integrated to this boilerplate thanks to babel-import-plugin.
[Theme customization is possible in antd](https://ant.design/docs/react/customize-theme), normally only with less variables.

As we use SASS in the boilerplate, support for SASS variable customization was added thanks to [antd-scss-theme-plugin](https://github.com/intoli/antd-scss-theme-plugin).
You can now tweaks variables in `src/style/theme.scss`.

For a list of available variables, refer to [Ant Design default theme](https://github.com/ant-design/ant-design/blob/master/components/style/themes/default.less)

__!! WARNING !!__
If you want to use the scss variables defined in theme.scss in another scss file, you need to import it with a relative path:
```sass
@import 'src/style/theme.scss'
```
Otherwise, the plugin is not aware that we import a file, and does not add the antd variables in the context of the importing file.
