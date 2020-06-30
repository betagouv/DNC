module.exports = {
  // Tell eslint not to seek a eslintrc file in parent folders.
  root: true,

  env: {
    es6: true,
    // Define jest globals in .spec.js and .test.js files.
    jest: true,
    // Define browser globals (window, document, etc.).
    browser: true,
  },

  plugins: ['jsdoc', 'react-hooks'],

  extends: 'airbnb',

  parser: 'babel-eslint',

  rules: {
    'react-hooks/exhaustive-deps': 'off',
    // Allow ++ operator.
    'no-plusplus': 'off',
    'no-useless-escape': 'off',
    // Allow named export without default export.
    'import/prefer-default-export': 'off',
    'no-nested-ternary': 'off',
    // On linebreak, enforce operator on the new line, except for the '?' of a ternary expression.
    'operator-linebreak': ['error', 'before'],

    // Require parenthesis around arrow functions only if multiple args or body.
    'arrow-parens': [
      'error',
      'as-needed',
      {
        requireForBlockBody: true,
      },
    ],

    // Prevent multiple empty lines. Allow 1 at EOF, 0 at BOF.
    'no-multiple-empty-lines': [
      'error',
      {
        max: 1,
        maxEOF: 1,
        maxBOF: 0,
      },
    ],

    // Enforce single quotes except for strings with single quotes in body.
    quotes: [
      'error',
      'single',
      {
        avoidEscape: true,
      },
    ],

    // Allow assigning in argument if object.
    'no-param-reassign': [
      'error',
      {
        props: false,
      },
    ],

    // Enforce one empty line at the end of the file.
    'eol-last': ['error', 'always'],

    // Always declare the state as a class property.
    'react/state-in-constructor': ['error', 'never'],

    // This is not working with defaultProps.
    // https://github.com/yannickcr/eslint-plugin-react/issues/1846
    'react/button-has-type': 'off',

    // This rule just makes the code more verbose and less readable.
    'react/destructuring-assignment': 'off',
    // Does not work well with inline text.
    'react/jsx-one-expression-per-line': 'off',
    // Allow props spreading.
    'react/jsx-props-no-spreading': 'off',

    // JSDoc specific rules.
    // Set most rules as warnings instead of errors.
    'jsdoc/check-alignment': 'warn',
    'jsdoc/check-param-names': 'warn',
    'jsdoc/check-syntax': 'warn',
    'jsdoc/check-tag-names': ['warn', { definedTags: ['async'] }],
    'jsdoc/check-types': 'warn',
    'jsdoc/implements-on-classes': 'warn',
    'jsdoc/match-description': 'warn',
    'jsdoc/newline-after-description': 'warn',
    'jsdoc/no-undefined-types': 'warn',
    'jsdoc/require-description-complete-sentence': 'warn',
    'jsdoc/require-hyphen-before-param-description': 'warn',
    'jsdoc/require-param': 'warn',
    'jsdoc/require-param-description': 'warn',
    'jsdoc/require-param-name': 'warn',
    'jsdoc/require-param-type': 'warn',
    'jsdoc/require-returns': 'warn',
    'jsdoc/require-returns-description': 'warn',
    'jsdoc/require-returns-check': 'warn',
    'jsdoc/require-returns-type': 'warn',
    'jsdoc/valid-types': 'warn',

    // Disable these rules.
    'jsdoc/require-description': 'off',
    'jsdoc/require-example': 'off',
    'import/no-cycle': 'off',

    'jsdoc/require-jsdoc': [
      'warn',
      {
        require: {
          FunctionDeclaration: true,
          MethodDefinition: true,
          ClassDeclaration: false,
          ArrowFunctionExpression: false,
          FunctionExpression: true,
        },
      },
    ],
    'react-hooks/rules-of-hooks': 'error', // Vérifie les règles des Hooks
  },

  settings: {
    'import/resolver': {
      node: {
        extensions: ['.js', '.jsx'],

        paths: ['./src/'],
      },
    },
  },

  overrides: [
    {
      files: ['src/**/*.stories.{js,jsx}', '.{jest,storybook,webpack}/**/*.{js,jsx}'],
      rules: {
        'import/no-extraneous-dependencies': ['error', { devDependencies: true }],

        // Storybook webpack babel only checks js files.
        'react/jsx-filename-extension': 0,
      },
    },
  ],
};
