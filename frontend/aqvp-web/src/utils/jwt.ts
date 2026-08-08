import type { User } from '@/types/auth';

interface JwtPayload {
  sub?: string;
  authorities?: string[];
  exp?: number;
  iat?: number;
}

function base64UrlDecode(value: string): string {
  const base64 = value.replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
  return window.atob(padded);
}

export function decodeJwt(token: string): JwtPayload | null {
  try {
    const payload = token.split('.')[1];
    if (!payload) return null;
    return JSON.parse(base64UrlDecode(payload)) as JwtPayload;
  } catch {
    return null;
  }
}

export function parseUserFromToken(token: string): User | null {
  const payload = decodeJwt(token);
  if (!payload?.sub) return null;
  const permissions = payload.authorities ?? [];
  return {
    id: payload.sub,
    username: payload.sub,
    email: '',
    firstName: payload.sub,
    lastName: '',
    enabled: true,
    roles: [],
    permissions,
  };
}

export function isTokenExpired(token: string): boolean {
  const payload = decodeJwt(token);
  if (!payload?.exp) return true;
  return payload.exp * 1000 < Date.now();
}
