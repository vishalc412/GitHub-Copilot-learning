import type { TaskStatus } from '../types/task';
import { STATUS_LABELS } from '../types/task';

interface StatusBadgeProps {
  status: TaskStatus;
}

/**
 * Small coloured pill showing a task's lifecycle state.
 *
 * The status→class mapping is a `Record`, not a chain of ternaries, so adding
 * a fourth status becomes a compile error here instead of a silently
 * unstyled badge at runtime.
 */
export function StatusBadge({ status }: StatusBadgeProps) {
  const className: Record<TaskStatus, string> = {
    TODO: 'badge badge-todo',
    IN_PROGRESS: 'badge badge-progress',
    DONE: 'badge badge-done',
  };

  return (
    <span className={className[status]} data-testid={`status-${status}`}>
      {STATUS_LABELS[status]}
    </span>
  );
}
