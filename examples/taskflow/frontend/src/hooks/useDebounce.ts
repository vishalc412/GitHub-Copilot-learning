import { useEffect, useState } from 'react';

/**
 * Returns a copy of `value` that only updates after `delay` ms of quiet.
 *
 * Used for the search box: without it, every keystroke fires a request and the
 * responses can arrive out of order. With it, one request fires per pause.
 *
 * @template T value type
 * @param value the rapidly-changing value
 * @param delay quiet period in milliseconds
 * @returns the debounced value
 *
 * @example
 * const [search, setSearch] = useState('');
 * const debouncedSearch = useDebounce(search, 300);
 * // pass debouncedSearch to your query, not search
 */
export function useDebounce<T>(value: T, delay = 300): T {
  const [debounced, setDebounced] = useState<T>(value);

  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delay);
    // Clearing on every change is what makes this a debounce rather than a
    // delay — a new keystroke cancels the pending update.
    return () => clearTimeout(timer);
  }, [value, delay]);

  return debounced;
}
