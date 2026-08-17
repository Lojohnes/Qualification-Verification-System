export const PASSWORD_PATTERN =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[-@$!%*?&#^()_+=[\]{}:;,.<>/~])[A-Za-z\d@$!%*?&#^()_+=[\]{}:;,.<>/~-]{8,}$/;

export const PASSWORD_HELP_TEXT =
  'At least 8 characters with uppercase, lowercase, a digit and a special character ' +
  '(e.g. @$!%*?&#^()_+-=)';
