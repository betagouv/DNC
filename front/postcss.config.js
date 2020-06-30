const autoprefixer = require('autoprefixer');

module.exports = {
  plugins: [
    autoprefixer({
      browsers: ['node 6', '> 1%'],
      grid: true,
    }),
  ],
};
