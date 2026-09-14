import { useState } from 'react';
import type { CreateTaskPayload, TaskPriority } from '../types/task';
import { PRIORITY_LABELS, PRIORITY_ORDER } from '../types/task';

interface TaskFormProps {
  saving: boolean;
  /** Field-level messages from the API, keyed by field name. */
  fieldErrors: Record<string, string>;
  onSubmit: (payload: CreateTaskPayload) => Promise<boolean>;
}

const EMPTY_FORM = {
  title: '',
  description: '',
  priority: 'MEDIUM' as TaskPriority,
  assignee: '',
  dueDate: '',
};

/**
 * Create-task form.
 *
 * Validation is deliberately split in two:
 *
 * - A cheap client-side check on title length, for instant feedback.
 * - Server-side messages from `fieldErrors`, rendered under the matching
 *   input.
 *
 * The client check is a convenience, never the authority — the backend's Bean
 * Validation rules are the contract, and this form displays whatever they say.
 */
export function TaskForm({ saving, fieldErrors, onSubmit }: TaskFormProps) {
  const [form, setForm] = useState(EMPTY_FORM);
  const [localError, setLocalError] = useState<string | null>(null);

  const update = <K extends keyof typeof EMPTY_FORM>(key: K, value: (typeof EMPTY_FORM)[K]) => {
    setForm((current) => ({ ...current, [key]: value }));
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const title = form.title.trim();
    if (title.length < 3) {
      setLocalError('Title must be at least 3 characters');
      return;
    }
    setLocalError(null);

    const payload: CreateTaskPayload = {
      title,
      description: form.description.trim() === '' ? null : form.description.trim(),
      priority: form.priority,
      assignee: form.assignee.trim() === '' ? null : form.assignee.trim(),
      dueDate: form.dueDate === '' ? null : form.dueDate,
    };

    const succeeded = await onSubmit(payload);
    if (succeeded) {
      setForm(EMPTY_FORM);
    }
  };

  const titleError = localError ?? fieldErrors['title'];

  return (
    <form className="task-form" onSubmit={handleSubmit} noValidate>
      <h2 className="task-form-heading">New task</h2>

      <div className="field">
        <label htmlFor="task-title">
          Title <span className="required">*</span>
        </label>
        <input
          id="task-title"
          value={form.title}
          onChange={(event) => update('title', event.target.value)}
          aria-invalid={titleError !== undefined}
          aria-describedby={titleError !== undefined ? 'task-title-error' : undefined}
          placeholder="What needs doing?"
        />
        {titleError !== undefined && (
          <p className="field-error" id="task-title-error" role="alert">
            {titleError}
          </p>
        )}
      </div>

      <div className="field">
        <label htmlFor="task-description">Description</label>
        <textarea
          id="task-description"
          rows={3}
          value={form.description}
          onChange={(event) => update('description', event.target.value)}
          placeholder="Optional detail, acceptance criteria, links…"
        />
        {fieldErrors['description'] !== undefined && (
          <p className="field-error" role="alert">
            {fieldErrors['description']}
          </p>
        )}
      </div>

      <div className="field-row">
        <div className="field">
          <label htmlFor="task-priority">Priority</label>
          <select
            id="task-priority"
            value={form.priority}
            onChange={(event) => update('priority', event.target.value as TaskPriority)}
          >
            {PRIORITY_ORDER.map((priority) => (
              <option key={priority} value={priority}>
                {PRIORITY_LABELS[priority]}
              </option>
            ))}
          </select>
        </div>

        <div className="field">
          <label htmlFor="task-assignee">Assignee</label>
          <input
            id="task-assignee"
            value={form.assignee}
            onChange={(event) => update('assignee', event.target.value)}
            placeholder="Who owns it?"
          />
          {fieldErrors['assignee'] !== undefined && (
            <p className="field-error" role="alert">
              {fieldErrors['assignee']}
            </p>
          )}
        </div>

        <div className="field">
          <label htmlFor="task-due">Due date</label>
          <input
            id="task-due"
            type="date"
            value={form.dueDate}
            onChange={(event) => update('dueDate', event.target.value)}
          />
        </div>
      </div>

      <button type="submit" className="btn btn-primary" disabled={saving}>
        {saving ? 'Creating…' : 'Create task'}
      </button>
    </form>
  );
}
