import '@testing-library/react';

/**
 * Vitest setup, loaded before every test file (see vite.config.ts).
 *
 * Keep this minimal: global setup that hides how a component actually behaves
 * makes tests pass for the wrong reasons. Per-test `fetch` stubs live in the
 * tests that need them.
 */

// jsdom has no fetch implementation in some environments; give tests a
// predictable default they can override with vi.stubGlobal.
if (typeof globalThis.fetch === 'undefined') {
  globalThis.fetch = (() =>
    Promise.reject(new Error('fetch was called without being stubbed'))) as typeof fetch;
}
