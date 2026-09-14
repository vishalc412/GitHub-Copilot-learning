import { StatusBadge } from './StatusBadge';
import type { Task, TaskStatus } from '../types/task';
import { PRIORITY_LABELS, STATUS_LABELS } from '../types/task';

interface TaskCardProps {
  task: Task;
  disabled?: boolean;
  onChangeStatus: (id: number, status: TaskStatus) => void;
  onDelete: (id: number) => void;
}

/**
 * A single task, with the lifecycle actions the backend will actually accept.
 *
 * The transition buttons are rendered from `task.allowedTransitions`, which
 * the API computes from the domain rules. The UI therefore cannot offer an
 * illegal move — no duplicated state machine on the client, and no 409 the
 * user has to interpret.
 */
export function TaskCard({ task, disabled = false, onChangeStatus, onDelete }: TaskCardProps) {
  return (
    <article className={`task-card${task.overdue ? ' task-card-overdue' : ''}`}>
      <header className="task-card-head">
        <div className="task-card-title-row">
          <span className={`priority-dot priority-${task.priority.toLowerCase()}`} aria-hidden="true" />
          <h3 className="task-card-title">{task.title}</h3>
        </div>
        <StatusBadge status={task.status} />
      </header>

      {task.description !== null && task.description.length > 0 && (
        <p className="task-card-description">{task.description}</p>
      )}

      <dl className="task-card-meta">
        <div>
          <dt>Priority</dt>
          <dd>{PRIORITY_LABELS[task.priority]}</dd>
        </div>
        <div>
          <dt>Assignee</dt>
          <dd>{task.assignee ?? 'Unassigned'}</dd>
        </div>
        <div>
          <dt>Due</dt>
          <dd>
            {task.dueDate ?? 'No date'}
            {task.overdue && <span className="overdue-flag"> · Overdue</span>}
          </dd>
        </div>
      </dl>

      <footer className="task-card-actions">
        {task.allowedTransitions.map((next) => (
          <button
            key={next}
            type="button"
            className="btn btn-sm"
            disabled={disabled}
            onClick={() => onChangeStatus(task.id, next)}
          >
            Move to {STATUS_LABELS[next]}
          </button>
        ))}

        {task.allowedTransitions.length === 0 && (
          <span className="task-card-terminal">Complete — no further transitions</span>
        )}

        <button
          type="button"
          className="btn btn-sm btn-danger"
          disabled={disabled}
          onClick={() => onDelete(task.id)}
          aria-label={`Delete ${task.title}`}
        >
          Delete
        </button>
      </footer>
    </article>
  );
}
