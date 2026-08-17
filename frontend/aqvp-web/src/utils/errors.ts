import { isAxiosError } from 'axios';

interface ApiErrorResponse {
  message?: string;
  fieldErrors?: Record<string, string>;
}

/**
 * Extracts a human-readable error message from an API error, preferring
 * field-level validation messages returned by the backend (e.g. Jakarta
 * Bean Validation failures) over the generic top-level message.
 */
export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (isAxiosError<ApiErrorResponse>(error)) {
    const data = error.response?.data;
    if (data?.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
      return Object.values(data.fieldErrors).join(' ');
    }
    if (data?.message) {
      return data.message;
    }
  }
  return fallback;
}
