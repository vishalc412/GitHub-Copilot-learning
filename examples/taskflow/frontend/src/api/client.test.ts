import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError, request, toQueryString } from './client';

/**
 * Tests for the fetch wrapper.
 *
 * These assert the three behaviours that are easy to get wrong and painful to
 * debug later: a 204 with no body, an error body that is problem+json, and an
 * error body that is not JSON at all.
 *
 * Assertions use plain DOM/value checks rather than jest-dom matchers — the
 * project deliberately keeps its dependency surface small.
 */
describe('api client', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  function stubFetch(response: Partial<Response> & { textBody?: string }) {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: response.ok ?? true,
      status: response.status ?? 200,
      text: () => Promise.resolve(response.textBody ?? ''),
    } as Response);
    vi.stubGlobal('fetch', fetchMock);
    return fetchMock;
  }

  describe('toQueryString', () => {
    it('omits undefined, null, and empty values', () => {
      const query = toQueryString({
        status: 'TODO',
        priority: undefined,
        assignee: null,
        q: '',
        page: 0,
      });

      // page=0 must survive: zero is a meaningful page number, not "absent".
      expect(query).toBe('?status=TODO&page=0');
    });

    it('returns an empty string when nothing is set', () => {
      expect(toQueryString({ a: undefined, b: null })).toBe('');
    });
  });

  describe('request', () => {
    it('parses a JSON body on success', async () => {
      stubFetch({ ok: true, status: 200, textBody: '{"id":1,"title":"Ship it"}' });

      const result = await request<{ id: number; title: string }>('/api/v1/tasks/1');

      expect(result).toEqual({ id: 1, title: 'Ship it' });
    });

    it('returns undefined for 204 No Content without parsing a body', async () => {
      stubFetch({ ok: true, status: 204 });

      const result = await request<void>('/api/v1/tasks/1', { method: 'DELETE' });

      expect(result).toBeUndefined();
    });

    it('sends a JSON content-type only when there is a body', async () => {
      const fetchMock = stubFetch({ ok: true, status: 200, textBody: '{}' });

      await request('/api/v1/tasks');
      const getInit = fetchMock.mock.calls[0]?.[1] as RequestInit;
      expect(getInit.headers).toEqual({});

      await request('/api/v1/tasks', { method: 'POST', body: { title: 'x' } });
      const postInit = fetchMock.mock.calls[1]?.[1] as RequestInit;
      expect(postInit.headers).toEqual({ 'Content-Type': 'application/json' });
      expect(postInit.body).toBe('{"title":"x"}');
    });

    it('throws ApiError carrying the problem+json body', async () => {
      stubFetch({
        ok: false,
        status: 409,
        textBody: JSON.stringify({
          type: 'https://taskflow.example/problems/illegal-status-transition',
          title: 'Illegal status transition',
          status: 409,
          detail: 'Illegal status transition: TODO -> DONE',
          from: 'TODO',
          to: 'DONE',
        }),
      });

      await expect(request('/api/v1/tasks/1/status', { method: 'PATCH' })).rejects.toThrow(
        ApiError,
      );

      try {
        await request('/api/v1/tasks/1/status', { method: 'PATCH' });
        expect.unreachable('should have thrown');
      } catch (error) {
        const apiError = error as ApiError;
        expect(apiError.status).toBe(409);
        expect(apiError.message).toBe('Illegal status transition: TODO -> DONE');
        expect(apiError.problem?.from).toBe('TODO');
        expect(apiError.isNetworkError).toBe(false);
      }
    });

    it('exposes validation field errors', async () => {
      stubFetch({
        ok: false,
        status: 400,
        textBody: JSON.stringify({
          title: 'Validation failed',
          status: 400,
          errors: { title: 'title must be between 3 and 140 characters' },
        }),
      });

      try {
        await request('/api/v1/tasks', { method: 'POST', body: { title: 'ab' } });
        expect.unreachable('should have thrown');
      } catch (error) {
        const apiError = error as ApiError;
        expect(apiError.fieldErrors['title']).toContain('between 3 and 140');
      }
    });

    it('survives an error response that is not JSON', async () => {
      // A proxy returning an HTML error page must not crash the parser.
      stubFetch({ ok: false, status: 502, textBody: '<html>Bad Gateway</html>' });

      try {
        await request('/api/v1/tasks');
        expect.unreachable('should have thrown');
      } catch (error) {
        const apiError = error as ApiError;
        expect(apiError.status).toBe(502);
        expect(apiError.message).toBe('Request failed (502)');
        expect(apiError.problem).toBeNull();
      }
    });

    it('reports a network failure as status 0 with actionable text', async () => {
      vi.stubGlobal(
        'fetch',
        vi.fn().mockRejectedValue(new TypeError('Failed to fetch')),
      );

      try {
        await request('/api/v1/tasks');
        expect.unreachable('should have thrown');
      } catch (error) {
        const apiError = error as ApiError;
        expect(apiError.isNetworkError).toBe(true);
        expect(apiError.message).toContain('backend running');
      }
    });

    it('rethrows an AbortError untouched so callers can ignore it', async () => {
      const abortError = new DOMException('The operation was aborted.', 'AbortError');
      vi.stubGlobal('fetch', vi.fn().mockRejectedValue(abortError));

      await expect(request('/api/v1/tasks')).rejects.toBe(abortError);
    });
  });
});
