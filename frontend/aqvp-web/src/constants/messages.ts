export const MESSAGES = {
  LOGIN_SUCCESS: 'Welcome back.',
  LOGIN_ERROR: 'Invalid username or password.',
  LOGOUT_SUCCESS: 'You have been logged out.',
  FORGOT_PASSWORD_SUCCESS: 'If the email exists, a reset link has been sent.',
  RESET_PASSWORD_SUCCESS: 'Your password has been reset. You can now sign in.',
  RESET_PASSWORD_INVALID_TOKEN: 'This reset link is invalid or missing a token.',
  SESSION_EXPIRED: 'Your session has expired. Please log in again.',
  ACCESS_DENIED: 'You do not have permission to view this page.',
  NOT_FOUND: 'The page you requested could not be found.',
  UNAUTHORIZED: 'Please log in to access this page.',
  GENERIC_ERROR: 'Something went wrong. Please try again.',
} as const;
