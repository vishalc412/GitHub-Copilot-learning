import { useMemo, useState } from 'react';
import { ErrorBanner } from './components/ErrorBanner';
import { StatsBar } from './components/StatsBar';
import { TaskFilters, type FilterState } from './components/TaskFilters';
import { TaskForm } from './components/TaskForm';
import { TaskList } from './components/TaskList';
import { useDebounce } from './hooks/useDebounce';
import { useTaskMutations } from './hooks/useTaskMutations';
import { useTasks } from './hooks/useTasks';
import type { TaskFilters as TaskQuery } from './types/task';

const PAGE_SIZE = 20;

const INITIAL_FILTERS: FilterState = {
  search: '',
  status: '',
  priority: '',
  overdueOnly: false,
};

/**
 * TaskFlow dashboard.
 *
 * Composition root: it owns filter and page state, derives the API query from
 * them, and wires the data hooks to the presentational components. Every child
 * is either fully controlled or purely presentational, which is what makes
 * them straightforward to test in isolation.
 */
export default function App() {
  const [filterState, setFilterState] = useState<FilterState>(INITIAL_FILTERS);
  const [page, setPage] = useState(0);

  // Debounce only the text input; dropdowns should feel instant.
  const debouncedSearch = useDebounce(filterState.search, 300);

  const query = useMemo<TaskQuery>(
    () => ({
      q: debouncedSearch === '' ? undefined : debouncedSearch,
      status: filterState.status === '' ? undefined : filterState.status,
      priority: filterState.priority === '' ? undefined : filterState.priority,
      overdueOnly: filterState.overdueOnly ? true : undefined,
      page,
      size: PAGE_SIZE,
    }),
    [debouncedSearch, filterState.status, filterState.priority, filterState.overdueOnly, page],
  );

  const { tasks, page: pageData, stats, loading, error, refetch } = useTasks(query);
  const mutations = useTaskMutations(refetch);

  const handleFilterChange = (next: FilterState) => {
    setFilterState(next);
    // Any filter change invalidates the current page offset.
    setPage(0);
  };

  return (
    <div className="app">
      <header className="app-header">
        <div className="app-header-inner">
          <div className="brand">
            <span className="brand-mark" aria-hidden="true" />
            <div>
              <h1>TaskFlow</h1>
              <p className="brand-sub">Spring Boot + React reference project</p>
            </div>
          </div>
          <a
            className="btn btn-ghost btn-sm"
            href="http://localhost:8080/swagger-ui.html"
            target="_blank"
            rel="noreferrer"
          >
            API docs
          </a>
        </div>
      </header>

      <main className="app-main">
        <ErrorBanner
          error={error ?? mutations.error}
          onRetry={refetch}
          onDismiss={mutations.clearError}
        />

        <StatsBar stats={stats} />

        <div className="layout">
          <div className="layout-main">
            <TaskFilters
              value={filterState}
              onChange={handleFilterChange}
              resultCount={pageData?.totalElements}
            />

            <TaskList
              tasks={tasks}
              loading={loading}
              mutating={mutations.saving}
              onChangeStatus={mutations.changeStatus}
              onDelete={mutations.remove}
            />

            {pageData !== null && pageData.totalPages > 1 && (
              <nav className="pagination" aria-label="Pagination">
                <button
                  type="button"
                  className="btn btn-sm"
                  disabled={pageData.first || loading}
                  onClick={() => setPage((current) => Math.max(0, current - 1))}
                >
                  ← Previous
                </button>
                <span className="pagination-status">
                  Page {pageData.page + 1} of {pageData.totalPages}
                </span>
                <button
                  type="button"
                  className="btn btn-sm"
                  disabled={pageData.last || loading}
                  onClick={() => setPage((current) => current + 1)}
                >
                  Next →
                </button>
              </nav>
            )}
          </div>

          <aside className="layout-side">
            <TaskForm
              saving={mutations.saving}
              fieldErrors={mutations.error?.fieldErrors ?? {}}
              onSubmit={mutations.create}
            />
          </aside>
        </div>
      </main>
    </div>
  );
}
