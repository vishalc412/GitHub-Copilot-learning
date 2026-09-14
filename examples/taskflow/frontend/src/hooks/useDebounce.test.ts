import { act, renderHook } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { useDebounce } from './useDebounce';

describe('useDebounce', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('returns the initial value immediately', () => {
    const { result } = renderHook(() => useDebounce('first', 300));

    expect(result.current).toBe('first');
  });

  it('does not update before the delay elapses', () => {
    const { result, rerender } = renderHook(({ value }) => useDebounce(value, 300), {
      initialProps: { value: 'first' },
    });

    rerender({ value: 'second' });
    act(() => {
      vi.advanceTimersByTime(299);
    });

    expect(result.current).toBe('first');
  });

  it('updates once the delay elapses', () => {
    const { result, rerender } = renderHook(({ value }) => useDebounce(value, 300), {
      initialProps: { value: 'first' },
    });

    rerender({ value: 'second' });
    act(() => {
      vi.advanceTimersByTime(300);
    });

    expect(result.current).toBe('second');
  });

  it('collapses rapid changes into a single final update', () => {
    const { result, rerender } = renderHook(({ value }) => useDebounce(value, 300), {
      initialProps: { value: 'a' },
    });

    // Simulate fast typing: each change must cancel the previous pending update.
    for (const value of ['ab', 'abc', 'abcd']) {
      rerender({ value });
      act(() => {
        vi.advanceTimersByTime(100);
      });
    }

    // 300ms total elapsed, but never 300ms of quiet — still the original value.
    expect(result.current).toBe('a');

    act(() => {
      vi.advanceTimersByTime(300);
    });

    expect(result.current).toBe('abcd');
  });

  it('works with non-string values', () => {
    const { result, rerender } = renderHook(({ value }) => useDebounce(value, 100), {
      initialProps: { value: { page: 0 } },
    });

    rerender({ value: { page: 2 } });
    act(() => {
      vi.advanceTimersByTime(100);
    });

    expect(result.current).toEqual({ page: 2 });
  });
});
