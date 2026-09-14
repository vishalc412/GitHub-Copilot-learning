import { useCallback, useState } from 'react';
import { ApiError } from '../api/client';
import { changeTaskStatus, createTask, deleteTask } from '../api/tasks';
import type { CreateTaskPayload, TaskStatus } from '../types/task';

export interface UseTaskMutationsResult {
  /** True while any mutation is in flight. */
  saving: boolean;
  /** The last mutation error, or null. Cleared at the start of each attempt. */
  error: ApiError | null;
  clearError: () => void;
  create: (payload: CreateTaskPayload) => Promise<boolean>;
  changeStatus: (id: number, status: TaskStatus) => Promise<boolean>;
  remove: (id: number) => Promise<boolean>;
}

/**
 * Write operations for tasks, with shared in-flight and error state.
 *
 * Every mutation resolves to a boolean rather than throwing, so callers can
 * write `if (await create(payload)) { ... }` without a try/catch at each call
 * site. The structured failure is available on `error` — including
 * `error.fieldErrors` for validation problems, which the form renders next to
 * the offending inputs.
 *
 * @param onSuccess called after any successful mutation, to trigger a refetch
 * @returns mutation functions plus shared state
 */
export function useTaskMutations(onSuccess?: () => void): UseTaskMutationsResult {
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);

  const clearError = useCallback(() => setError(null), []);

  const run = useCallback(
    async (operation: () => Promise<unknown>): Promise<boolean> => {
      setSaving(true);
      setError(null);
      try {
        await operation();
        onSuccess?.();
        return true;
      } catch (cause) {
        setError(
          cause instanceof ApiError
            ? cause
            : new ApiError('Unexpected error while saving', 0),
        );
        return false;
      } finally {
        setSaving(false);
      }
    },
    [onSuccess],
  );

  const create = useCallback(
    (payload: CreateTaskPayload) => run(() => createTask(payload)),
    [run],
  );

  const changeStatus = useCallback(
    (id: number, status: TaskStatus) => run(() => changeTaskStatus(id, status)),
    [run],
  );

  const remove = useCallback((id: number) => run(() => deleteTask(id)), [run]);

  return { saving, error, clearError, create, changeStatus, remove };
}
