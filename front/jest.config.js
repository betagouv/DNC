module.exports = {
  verbose: true,

  // Exit on error.
  bail: true,

  // https://github.com/facebook/jest/issues/6769.
  testURL: 'http://localhost/',

  // Ignore node_modules test files.
  testPathIgnorePatterns: [
    '<rootDir>/node_modules/',
  ],

  // Absolute import paths.
  modulePaths: [
    '<rootDir>/src',
  ],

  // Transform with babel-jest.
  transform: {
    '^.+\\.(js|jsx)$': '<rootDir>/node_modules/babel-jest',
  },

  setupFiles: [
    '<rootDir>/.jest/setup.js',
  ],
};
