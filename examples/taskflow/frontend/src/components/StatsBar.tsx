import type { TaskStats } from '../types/task';

interface StatsBarProps {
  stats: TaskStats | null;
}

/**
 * Dashboard header showing counts by status and overall progress.
 *
 * Renders skeleton placeholders rather than collapsing to nothing while stats
 * load, so the page does not jump when the numbers arrive.
 */
export function StatsBar({ stats }: StatsBarProps) {
  const percent = stats ? Math.round(stats.completionRate * 100) : 0;

  return (
    <section className="stats" aria-label="Task statistics">
      <div className="stats-grid">
        <StatTile label="Total" value={stats?.total} />
        <StatTile label="To do" value={stats?.todo} />
        <StatTile label="In progress" value={stats?.inProgress} />
        <StatTile label="Done" value={stats?.done} />
      </div>

      <div className="progress-wrap">
        <div className="progress-label">
          <span>Completion</span>
          <strong>{stats ? `${percent}%` : '—'}</strong>
        </div>
        <div
          className="progress-track"
          role="progressbar"
          aria-valuenow={percent}
          aria-valuemin={0}
          aria-valuemax={100}
          aria-label="Completion rate"
        >
          <div className="progress-fill" style={{ width: `${percent}%` }} />
        </div>
      </div>
    </section>
  );
}

function StatTile({ label, value }: { label: string; value: number | undefined }) {
  return (
    <div className="stat-tile">
      <span className="stat-value">{value ?? '—'}</span>
      <span className="stat-label">{label}</span>
    </div>
  );
}
