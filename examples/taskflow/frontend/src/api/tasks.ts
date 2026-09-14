import { request, toQueryString } from './client';
import type {
  CreateTaskPayload,
  Page,
  Task,
  TaskFilters,
  TaskStats,
  TaskStatus,
  UpdateTaskPayload,
} from '../types/task';

/**
 * Typed bindings for `/api/v1/tasks`.
 *
 * One function per endpoint, named after the operation, with the request and
 * response types spelled out. This is the file to open before asking Copilot
 * to write a component that talks to the API — with these signatures in
 * context it generates correct calls instead of inventing plausible ones.
 */

const BASE = '/api/v1/tasks';

/**
 * Lists tasks with optional filters, sorting, and pagination.
 *
 * @param filters status, priority, assignee, search text, paging, and sort
 * @param signal optional abort signal, for cancelling a superseded search
 * @returns a page of tasks
 */
export function listTasks(filters: TaskFilters = {}, signal?: AbortSignal): Promise<Page<Task>> {
  const query = toQueryString({
    status: filters.status,
    priority: filters.priority,
    assignee: filters.assignee,
    q: filters.q,
    overdueOnly: filters.overdueOnly ? 'true' : undefined,
    page: filters.page,
    size: filters.size,
    sort: filters.sort,
  });
  return request<Page<Task>>(`${BASE}${query}`, signal ? { signal } : {});
}

/**
 * @param id task identifier
 * @returns the task
 */
export function getTask(id: number): Promise<Task> {
  return request<Task>(`${BASE}/${id}`);
}

/**
 * @param payload new task fields
 * @returns the created task, including its server-assigned id
 */
export function createTask(payload: CreateTaskPayload): Promise<Task> {
  return request<Task>(BASE, { method: 'POST', body: payload });
}

/**
 * @param id task identifier
 * @param payload replacement field values
 * @returns the updated task
 */
export function updateTask(id: number, payload: UpdateTaskPayload): Promise<Task> {
  return request<Task>(`${BASE}/${id}`, { method: 'PUT', body: payload });
}

/**
 * Moves a task through its lifecycle. The backend rejects illegal transitions
 * with 409, so render buttons from `task.allowedTransitions` rather than
 * offering every status.
 *
 * @param id task identifier
 * @param status desired next state
 * @returns the updated task
 */
export function changeTaskStatus(id: number, status: TaskStatus): Promise<Task> {
  return request<Task>(`${BASE}/${id}/status`, { method: 'PATCH', body: { status } });
}

/**
 * @param id task identifier
 */
export function deleteTask(id: number): Promise<void> {
  return request<void>(`${BASE}/${id}`, { method: 'DELETE' });
}

/**
 * @returns aggregate counts and completion rate
 */
export function getTaskStats(): Promise<TaskStats> {
  return request<TaskStats>(`${BASE}/stats`);
}
