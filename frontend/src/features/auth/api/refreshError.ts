import axios from 'axios';

const INVALID_REFRESH_STATUSES = new Set([400, 401, 403, 422]);

export function shouldClearSessionAfterRefreshError(error: unknown): boolean {
  if (!axios.isAxiosError(error)) {
    return false;
  }

  const status = error.response?.status;
  return status !== undefined && INVALID_REFRESH_STATUSES.has(status);
}
