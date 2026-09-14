import type { ProblemDetail } from '../types/task';

/**
 * Base URL for API calls.
 *
 * Empty by default so requests are same-origin and Vite's dev proxy handles
 * them (see vite.config.ts). Set `VITE_API_BASE_URL` to point at a backend on
 * another host.
 */
const API_BASE_URL: string = import.meta.env['VITE_API_BASE_URL'] ?? '';

/**
 * A failed API call, carrying the parsed RFC 9457 problem+json body.
 *
 * Every non-2xx response becomes one of these, so callers have a single
 * exception type to catch and a structured body to render — no string
 * matching on error messages.
 */
export class ApiError extends Error {
  /** HTTP status, or 0 when the request never reached the server. */
  readonly status: number;
  /** Parsed problem+json body, when the server sent one. */
  readonly problem: ProblemDetail | null;

  constructor(message: string, status: number, problem: ProblemDetail | null = null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.problem = problem;
  }

  /** @returns field-level validation messages, empty when not a validation error */
  get fieldErrors(): Record<string, string> {
    return this.problem?.errors ?? {};
  }

  /** @returns true when the request failed before reaching the server */
  get isNetworkError(): boolean {
    return this.status === 0;
  }
}

/** Options accepted by {@link request}, minus the parts we set ourselves. */
interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: unknown;
  signal?: AbortSignal;
}

/**
 * Performs a JSON API request and returns the parsed body.
 *
 * Handles the three cases that bite hand-rolled fetch wrappers: a 204 with no
 * body, an error response whose body is problem+json, and an error response
 * whose body is not JSON at all.
 *
 * @template T expected response shape
 * @param path API path beginning with a slash, e.g. `/api/v1/tasks`
 * @param options method, body, and abort signal
 * @returns the parsed response body, or `undefined` for 204 No Content
 * @throws {ApiError} on any non-2xx response or network failure
 */
export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, signal } = options;

  let response: Response;
  try {
    const init: RequestInit = {
      method,
      headers: body === undefined ? {} : { 'Content-Type': 'application/json' },
    };
    if (body !== undefined) {
      init.body = JSON.stringify(body);
    }
    if (signal) {
      init.signal = signal;
    }
    response = await fetch(`${API_BASE_URL}${path}`, init);
  } catch (cause) {
    // An aborted request is a normal control-flow event, not an error to render.
    if (cause instanceof DOMException && cause.name === 'AbortError') {
      throw cause;
    }
    throw new ApiError('Could not reach the server. Is the backend running?', 0);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const rawBody = await response.text();
  const parsed: unknown = rawBody.length > 0 ? safeJsonParse(rawBody) : null;

  if (!response.ok) {
    const problem = isProblemDetail(parsed) ? parsed : null;
    const message = problem?.detail ?? problem?.title ?? `Request failed (${response.status})`;
    throw new ApiError(message, response.status, problem);
  }

  return parsed as T;
}

/** Builds a query string, omitting empty, null, and undefined values. */
export function toQueryString(params: Record<string, unknown>): string {
  const search = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null || value === '') {
      continue;
    }
    search.set(key, String(value));
  }
  const query = search.toString();
  return query.length > 0 ? `?${query}` : '';
}

function safeJsonParse(text: string): unknown {
  try {
    return JSON.parse(text);
  } catch {
    // A proxy or load balancer returning an HTML error page lands here.
    return null;
  }
}

function isProblemDetail(value: unknown): value is ProblemDetail {
  return typeof value === 'object' && value !== null;
}
