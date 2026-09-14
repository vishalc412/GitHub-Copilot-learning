import { TaskCard } from './TaskCard';
import type { Task, TaskStatus } from '../types/task';

interface TaskListProps {
  tasks: Task[];
  loading: boolean;
  mutating: boolean;
  onChangeStatus: (id: number, status: TaskStatus) => void;
  onDelete: (id: number) => void;
}

/**
 * The task collection, with explicit loading and empty states.
 *
 * Both states are real UI, not afterthoughts: a list that renders nothing
 * while loading looks broken, and one that renders nothing when empty looks
 * like a failure rather than a clean slate.
 */
export function TaskList({ tasks, loading, mutating, onChangeStatus, onDelete }: TaskListProps) {
  if (loading) {
    return (
      <div className="task-list" aria-busy="true" aria-live="polite">
        {[0, 1, 2].map((index) => (
          <div key={index} className="task-card task-card-skeleton" />
        ))}
      </div>
    );
  }

  if (tasks.length === 0) {
    return (
      <div className="empty-state">
        <h3>No tasks match these filters</h3>
        <p>Clear the filters, or create a task to get started.</p>
      </div>
    );
  }

  return (
    <div className="task-list">
      {tasks.map((task) => (
        <TaskCard
          key={task.id}
          task={task}
          disabled={mutating}
          onChangeStatus={onChangeStatus}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}
