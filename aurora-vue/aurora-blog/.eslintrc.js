module.exports = {
  root: true,
  env: { browser: true, es2021: true, node: true },
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@typescript-eslint/parser',
    ecmaVersion: 'latest',
    sourceType: 'module'
  },
  plugins: ['@typescript-eslint'],
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:vue/vue3-recommended',
    'prettier'
  ],
  rules: {
    // Vue: allow single-word component names (common pattern)
    'vue/multi-word-component-names': 'off',
    'vue/no-v-html': 'off',
    'vue/no-reserved-component-names': 'off',
    'vue/no-unused-components': 'off',
    'vue/no-deprecated-v-on-native-modifier': 'off',
    'vue/valid-v-on': 'off',
    'vue/require-toggle-inside-transition': 'off',
    'vue/no-parsing-error': 'off',
    'vue/prefer-import-from-vue': 'off',
    'vue/component-definition-name-casing': 'off',
    // TypeScript: warn on any, handle gradually
    '@typescript-eslint/no-explicit-any': 'warn',
    '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_', varsIgnorePattern: '^_' }],
    '@typescript-eslint/no-var-requires': 'off',
    '@typescript-eslint/ban-ts-comment': 'off',
    // General: allow patterns used in project
    'no-console': 'off',
    'no-case-declarations': 'off',
    'no-empty-pattern': 'off',
    'valid-typeof': 'off',
    'no-empty': ['error', { allowEmptyCatch: true }]
  },
  overrides: [
    { files: ['*.vue'], rules: { 'no-undef': 'off' } }
  ]
}
