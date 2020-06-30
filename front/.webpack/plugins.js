const path = require('path');

const AntdScssThemePlugin = require('antd-scss-theme-plugin');

const antdScssThemePlugin = new AntdScssThemePlugin(path.resolve(__dirname, '../src/style/theme.scss'));

module.exports = {
  antdScssThemePlugin,
};
