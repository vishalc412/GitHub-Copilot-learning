import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { TaskCard } from './TaskCard';
import type { Task } from '../types/task';

/**
 * Tests for the task card.
 *
 * The central behaviour worth protecting: transition buttons come from
 * `allowedTransitions` supplied by the API, so the UI can never offer a move
 * the backend would reject with 409.
 */
describe('TaskCard', () => {
  function buildTask(overrides: Partial<Task> = {}): Task {
    return {
      id: 1,
      title: 'Migrate auth service',
      description: 'Replace the legacy cookie flow',
      status: 'TODO',
      priority: 'HIGH',
      assignee: 'priya',
      dueDate: '2026-12-01',
      overdue: false,
      allowedTransitions: ['IN_PROGRESS'],
      createdAt: '2026-09-01T10:00:00Z',
      updatedAt: '2026-09-01T10:00:00Z',
      version: 0,
      ...overrides,
    };
  }

  it('renders the task details', () => {
    render(
      <TaskCard task={buildTask()} onChangeStatus={vi.fn()} onDelete={vi.fn()} />,
    );

    screen.getByRole('heading', { name: 'Migrate auth service' });
    screen.getByText('Replace the legacy cookie flow');
    screen.getByText('priya');
    screen.getByText('High');
    screen.getByTestId('status-TODO');
  });

  it('shows "Unassigned" when there is no assignee', () => {
    render(
      <TaskCard
        task={buildTask({ assignee: null })}
        onChangeStatus={vi.fn()}
        onDelete={vi.fn()}
      />,
    );

    screen.getByText('Unassigned');
  });

  it('omits the description block when there is no description', () => {
    render(
      <TaskCard
        task={buildTask({ description: null })}
        onChangeStatus={vi.fn()}
        onDelete={vi.fn()}
      />,
    );

    expect(screen.queryByText('Replace the legacy cookie flow')).toBeNull();
  });

  it('renders one button per allowed transition and nothing more', () => {
    render(
      <TaskCard
        task={buildTask({ status: 'IN_PROGRESS', allowedTransitions: ['TODO', 'DONE'] })}
        onChangeStatus={vi.fn()}
        onDelete={vi.fn()}
      />,
    );

    screen.getByRole('button', { name: 'Move to To do' });
    screen.getByRole('button', { name: 'Move to Done' });
    // IN_PROGRESS -> IN_PROGRESS is not offered.
    expect(screen.queryByRole('button', { name: 'Move to In progress' })).toBeNull();
  });

  it('shows a terminal message instead of buttons when no transitions remain', () => {
    render(
      <TaskCard
        task={buildTask({ status: 'DONE', allowedTransitions: [] })}
        onChangeStatus={vi.fn()}
        onDelete={vi.fn()}
      />,
    );

    screen.getByText(/no further transitions/i);
    expect(screen.queryByRole('button', { name: /^Move to/ })).toBeNull();
  });

  it('calls onChangeStatus with the task id and target status', async () => {
    const onChangeStatus = vi.fn();
    const user = userEvent.setup();

    render(
      <TaskCard task={buildTask()} onChangeStatus={onChangeStatus} onDelete={vi.fn()} />,
    );

    await user.click(screen.getByRole('button', { name: 'Move to In progress' }));

    expect(onChangeStatus).toHaveBeenCalledWith(1, 'IN_PROGRESS');
  });

  it('calls onDelete with the task id', async () => {
    const onDelete = vi.fn();
    const user = userEvent.setup();

    render(<TaskCard task={buildTask()} onChangeStatus={vi.fn()} onDelete={onDelete} />);

    await user.click(screen.getByRole('button', { name: 'Delete Migrate auth service' }));

    expect(onDelete).toHaveBeenCalledWith(1);
  });

  it('flags an overdue task', () => {
    render(
      <TaskCard
        task={buildTask({ overdue: true, dueDate: '2026-01-01' })}
        onChangeStatus={vi.fn()}
        onDelete={vi.fn()}
      />,
    );

    screen.getByText(/Overdue/);
  });

  it('disables every action while a mutation is in flight', () => {
    render(
      <TaskCard
        task={buildTask()}
        disabled
        onChangeStatus={vi.fn()}
        onDelete={vi.fn()}
      />,
    );

    for (const button of screen.getAllByRole('button')) {
      expect(button).toHaveProperty('disabled', true);
    }
  });
});
