import { useCallback, useEffect, useMemo, useState } from 'react';
import { ApiError } from '../api/client';
import { getTaskStats, listTasks } from '../api/tasks';
import type { Page, Task, TaskFilters, TaskStats } from '../types/task';

export interface UseTasksResult {
  tasks: Task[];
  page: Page<Task> | null;
  stats: TaskStats | null;
  loading: boolean;
  error: ApiError | null;
  /** Re-runs the current query and refreshes stats. */
  refetch: () => void;
}

/**
 * Loads the task list and stats for a set of filters.
 *
 * Two details that matter more than they look:
 *
 * 1. The effect depends on a *serialized* copy of `filters`, not the object
 *    itself. A caller that builds the filter object inline would otherwise
 *    hand us a new identity on every render and loop forever.
 * 2. Each run gets an `AbortController` and the cleanup aborts it. Typing in
 *    the search box fires overlapping requests, and without this the slowest
 *    response wins — showing results for a query the user already changed.
 *
 * @param filters current query; may be rebuilt inline by the caller
 * @returns tasks, paging envelope, stats, loading and error state, and refetch
 */
export function useTasks(filters: TaskFilters): UseTasksResult {
  const [page, setPage] = useState<Page<Task> | null>(null);
  const [stats, setStats] = useState<TaskStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<ApiError | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  // Depend on the filter *value*, so an inline object literal is safe.
  const filterKey = useMemo(() => JSON.stringify(filters), [filters]);

  useEffect(() => {
    const controller = new AbortController();
    let active = true;

    setLoading(true);
    setError(null);

    const currentFilters = JSON.parse(filterKey) as TaskFilters;

    void (async () => {
      try {
        const [pageResult, statsResult] = await Promise.all([
          listTasks(currentFilters, controller.signal),
          getTaskStats(),
        ]);
        if (active) {
          setPage(pageResult);
          setStats(statsResult);
        }
      } catch (cause) {
        // An abort means a newer request superseded this one — not an error.
        if (controller.signal.aborted || !active) {
          return;
        }
        setError(
          cause instanceof ApiError
            ? cause
            : new ApiError('Something went wrong loading tasks', 0),
        );
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    })();

    return () => {
      active = false;
      controller.abort();
    };
  }, [filterKey, reloadToken]);

  const refetch = useCallback(() => {
    setReloadToken((token) => token + 1);
  }, []);

  return {
    tasks: page?.content ?? [],
    page,
    stats,
    loading,
    error,
    refetch,
  };
}
