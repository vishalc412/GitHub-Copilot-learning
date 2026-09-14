import type { TaskPriority, TaskStatus } from '../types/task';
import { PRIORITY_LABELS, PRIORITY_ORDER, STATUS_LABELS } from '../types/task';

export interface FilterState {
  search: string;
  status: TaskStatus | '';
  priority: TaskPriority | '';
  overdueOnly: boolean;
}

interface TaskFiltersProps {
  value: FilterState;
  onChange: (next: FilterState) => void;
  resultCount: number | undefined;
}

const STATUS_OPTIONS: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];

/**
 * Filter controls for the task list.
 *
 * Fully controlled: the parent owns `FilterState` and passes it down, so the
 * search box, the query sent to the API, and the URL (if you later add
 * routing) can never disagree with each other.
 */
export function TaskFilters({ value, onChange, resultCount }: TaskFiltersProps) {
  const update = <K extends keyof FilterState>(key: K, next: FilterState[K]) => {
    onChange({ ...value, [key]: next });
  };

  const hasActiveFilters =
    value.search !== '' || value.status !== '' || value.priority !== '' || value.overdueOnly;

  return (
    <section className="filters" aria-label="Filter tasks">
      <div className="filters-row">
        <div className="field field-grow">
          <label htmlFor="filter-search">Search</label>
          <input
            id="filter-search"
            type="search"
            placeholder="Title or description…"
            value={value.search}
            onChange={(event) => update('search', event.target.value)}
          />
        </div>

        <div className="field">
          <label htmlFor="filter-status">Status</label>
          <select
            id="filter-status"
            value={value.status}
            onChange={(event) => update('status', event.target.value as TaskStatus | '')}
          >
            <option value="">All</option>
            {STATUS_OPTIONS.map((status) => (
              <option key={status} value={status}>
                {STATUS_LABELS[status]}
              </option>
            ))}
          </select>
        </div>

        <div className="field">
          <label htmlFor="filter-priority">Priority</label>
          <select
            id="filter-priority"
            value={value.priority}
            onChange={(event) => update('priority', event.target.value as TaskPriority | '')}
          >
            <option value="">All</option>
            {PRIORITY_ORDER.map((priority) => (
              <option key={priority} value={priority}>
                {PRIORITY_LABELS[priority]}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="filters-row filters-row-secondary">
        <label className="checkbox">
          <input
            type="checkbox"
            checked={value.overdueOnly}
            onChange={(event) => update('overdueOnly', event.target.checked)}
          />
          Overdue only
        </label>

        <span className="result-count" aria-live="polite">
          {resultCount === undefined ? '' : `${resultCount} task${resultCount === 1 ? '' : 's'}`}
        </span>

        {hasActiveFilters && (
          <button
            type="button"
            className="btn btn-sm btn-ghost"
            onClick={() =>
              onChange({ search: '', status: '', priority: '', overdueOnly: false })
            }
          >
            Clear filters
          </button>
        )}
      </div>
    </section>
  );
}
