import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { TaskForm } from './TaskForm';

/**
 * Tests for the create-task form.
 *
 * Covers the split validation model: the cheap client-side check that gives
 * instant feedback, and server-supplied field errors rendered next to the
 * matching input.
 */
describe('TaskForm', () => {
  it('submits a normalised payload', async () => {
    const onSubmit = vi.fn().mockResolvedValue(true);
    const user = userEvent.setup();

    render(<TaskForm saving={false} fieldErrors={{}} onSubmit={onSubmit} />);

    await user.type(screen.getByLabelText(/Title/), '  Ship the release  ');
    await user.type(screen.getByLabelText('Description'), '  with notes  ');
    await user.selectOptions(screen.getByLabelText('Priority'), 'URGENT');
    await user.type(screen.getByLabelText('Assignee'), '  sam  ');
    await user.click(screen.getByRole('button', { name: 'Create task' }));

    expect(onSubmit).toHaveBeenCalledWith({
      title: 'Ship the release',
      description: 'with notes',
      priority: 'URGENT',
      assignee: 'sam',
      dueDate: null,
    });
  });

  it('converts blank optional fields to null rather than empty strings', async () => {
    const onSubmit = vi.fn().mockResolvedValue(true);
    const user = userEvent.setup();

    render(<TaskForm saving={false} fieldErrors={{}} onSubmit={onSubmit} />);

    await user.type(screen.getByLabelText(/Title/), 'Only a title');
    await user.click(screen.getByRole('button', { name: 'Create task' }));

    expect(onSubmit).toHaveBeenCalledWith({
      title: 'Only a title',
      description: null,
      priority: 'MEDIUM',
      assignee: null,
      dueDate: null,
    });
  });

  it('blocks submission and shows an error for a too-short title', async () => {
    const onSubmit = vi.fn();
    const user = userEvent.setup();

    render(<TaskForm saving={false} fieldErrors={{}} onSubmit={onSubmit} />);

    await user.type(screen.getByLabelText(/Title/), 'ab');
    await user.click(screen.getByRole('button', { name: 'Create task' }));

    expect(onSubmit).not.toHaveBeenCalled();
    screen.getByRole('alert');
    screen.getByText('Title must be at least 3 characters');
  });

  it('marks the title input invalid for accessibility when it errors', async () => {
    const user = userEvent.setup();

    render(<TaskForm saving={false} fieldErrors={{}} onSubmit={vi.fn()} />);

    const title = screen.getByLabelText(/Title/);
    await user.type(title, 'ab');
    await user.click(screen.getByRole('button', { name: 'Create task' }));

    expect(title.getAttribute('aria-invalid')).toBe('true');
    expect(title.getAttribute('aria-describedby')).toBe('task-title-error');
  });

  it('renders server-supplied field errors', () => {
    render(
      <TaskForm
        saving={false}
        fieldErrors={{
          title: 'title must be between 3 and 140 characters',
          assignee: 'assignee must not exceed 80 characters',
        }}
        onSubmit={vi.fn()}
      />,
    );

    screen.getByText('title must be between 3 and 140 characters');
    screen.getByText('assignee must not exceed 80 characters');
  });

  it('clears the form after a successful submit', async () => {
    const onSubmit = vi.fn().mockResolvedValue(true);
    const user = userEvent.setup();

    render(<TaskForm saving={false} fieldErrors={{}} onSubmit={onSubmit} />);

    const title = screen.getByLabelText(/Title/) as HTMLInputElement;
    await user.type(title, 'Transient task');
    await user.click(screen.getByRole('button', { name: 'Create task' }));

    expect(title.value).toBe('');
  });

  it('keeps the input values when submit fails, so work is not lost', async () => {
    const onSubmit = vi.fn().mockResolvedValue(false);
    const user = userEvent.setup();

    render(<TaskForm saving={false} fieldErrors={{}} onSubmit={onSubmit} />);

    const title = screen.getByLabelText(/Title/) as HTMLInputElement;
    await user.type(title, 'Duplicate title');
    await user.click(screen.getByRole('button', { name: 'Create task' }));

    expect(title.value).toBe('Duplicate title');
  });

  it('disables the submit button and shows progress while saving', () => {
    render(<TaskForm saving fieldErrors={{}} onSubmit={vi.fn()} />);

    const button = screen.getByRole('button', { name: 'Creating…' });
    expect(button).toHaveProperty('disabled', true);
  });
});
