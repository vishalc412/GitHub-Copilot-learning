/**
 * Types mirroring the backend API contract.
 *
 * Keeping these in one file, named exactly as the Java DTOs are, is what lets
 * Copilot generate correct API calls and components: it can see the shape of
 * every field without guessing. When the backend changes, change this file
 * first — everything downstream then fails to compile in exactly the right
 * places.
 *
 * Source of truth: `backend/src/main/java/com/example/taskflow/web/dto/`
 */

/** Lifecycle state. Mirrors `TaskStatus.java`. */
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

/** Urgency, ordered lowest to highest. Mirrors `TaskPriority.java`. */
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

/** A task as returned by the API. Mirrors `TaskResponse.java`. */
export interface Task {
  id: number;
  title: string;
  description: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  assignee: string | null;
  dueDate: string | null;
  /** Computed server-side — do not re-derive in the UI. */
  overdue: boolean;
  /** States this task may legally move to. Render buttons from this. */
  allowedTransitions: TaskStatus[];
  createdAt: string;
  updatedAt: string;
  version: number;
}

/** Payload for creating a task. Mirrors `CreateTaskRequest.java`. */
export interface CreateTaskPayload {
  title: string;
  description?: string | null;
  priority?: TaskPriority;
  assignee?: string | null;
  dueDate?: string | null;
}

/** Payload for a full update. Mirrors `UpdateTaskRequest.java`. */
export interface UpdateTaskPayload {
  title: string;
  description?: string | null;
  priority: TaskPriority;
  assignee?: string | null;
  dueDate?: string | null;
}

/** Aggregate counts. Mirrors `TaskStatsResponse.java`. */
export interface TaskStats {
  total: number;
  todo: number;
  inProgress: number;
  done: number;
  /** 0.0–1.0 */
  completionRate: number;
}

/** Page envelope. Mirrors `PageResponse.java`. */
export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

/** Query parameters accepted by `GET /api/v1/tasks`. */
export interface TaskFilters {
  status?: TaskStatus | undefined;
  priority?: TaskPriority | undefined;
  assignee?: string | undefined;
  q?: string | undefined;
  overdueOnly?: boolean | undefined;
  page?: number | undefined;
  size?: number | undefined;
  sort?: string | undefined;
}

/**
 * RFC 9457 problem+json body, as produced by `GlobalExceptionHandler`.
 * `errors` is present only on validation failures.
 */
export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  timestamp?: string;
  errors?: Record<string, string>;
  from?: TaskStatus;
  to?: TaskStatus;
  conflictingTitle?: string;
}

/** Display order and labels for priorities, highest urgency first. */
export const PRIORITY_ORDER: readonly TaskPriority[] = [
  'URGENT',
  'HIGH',
  'MEDIUM',
  'LOW',
] as const;

export const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: 'To do',
  IN_PROGRESS: 'In progress',
  DONE: 'Done',
};

export const PRIORITY_LABELS: Record<TaskPriority, string> = {
  LOW: 'Low',
  MEDIUM: 'Medium',
  HIGH: 'High',
  URGENT: 'Urgent',
};
