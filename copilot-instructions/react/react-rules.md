# React/TypeScript Copilot Instructions

## Hierarchy Level: Language-Specific

This file contains React and TypeScript-specific rules for GitHub Copilot. These rules extend the root `.github/copilot-instructions.md`.

---

## TypeScript Standards

### Interface Definitions (Required)
```typescript
// Always define interfaces for props, state, and API responses

interface UserProps {
  /** User's unique identifier */
  id: string;
  /** User's display name */
  name: string;
  /** User's email address */
  email: string;
  /** Optional callback when user is clicked */
  onClick?: (id: string) => void;
  /** Additional CSS classes */
  className?: string;
}

// Use type for unions and simple types
type ButtonVariant = 'primary' | 'secondary' | 'danger';
type LoadingState = 'idle' | 'loading' | 'success' | 'error';
```

### JSDoc for Components
```typescript
/**
 * Displays user information in a card format.
 *
 * @component
 * @example
 * <UserCard
 *   id="123"
 *   name="John Doe"
 *   email="john@example.com"
 *   onClick={(id) => console.log(id)}
 * />
 */
export const UserCard: React.FC<UserProps> = ({
  id,
  name,
  email,
  onClick,
  className = ''
}) => {
  // Implementation
};
```

---

## Component Patterns

### Functional Component with Hooks
```typescript
import React, { useState, useEffect, useCallback } from 'react';

interface CounterProps {
  initialValue?: number;
  min?: number;
  max?: number;
  onChange?: (value: number) => void;
}

/**
 * Interactive counter component with bounds.
 */
export const Counter: React.FC<CounterProps> = ({
  initialValue = 0,
  min = -Infinity,
  max = Infinity,
  onChange
}) => {
  const [count, setCount] = useState(initialValue);

  // Memoized handlers
  const increment = useCallback(() => {
    setCount(prev => {
      const newValue = Math.min(prev + 1, max);
      onChange?.(newValue);
      return newValue;
    });
  }, [max, onChange]);

  const decrement = useCallback(() => {
    setCount(prev => {
      const newValue = Math.max(prev - 1, min);
      onChange?.(newValue);
      return newValue;
    });
  }, [min, onChange]);

  return (
    <div className="counter">
      <button
        onClick={decrement}
        disabled={count <= min}
        aria-label="Decrement"
      >
        -
      </button>
      <span aria-live="polite">{count}</span>
      <button
        onClick={increment}
        disabled={count >= max}
        aria-label="Increment"
      >
        +
      </button>
    </div>
  );
};
```

### Generic Component
```typescript
interface TableProps<T> {
  data: T[];
  columns: Array<{
    key: keyof T;
    title: string;
    render?: (value: T[keyof T], item: T) => React.ReactNode;
  }>;
  onRowClick?: (item: T) => void;
  keyExtractor: (item: T) => string | number;
  loading?: boolean;
  emptyMessage?: string;
}

/**
 * Generic table component with TypeScript support.
 */
export function Table<T extends Record<string, unknown>>({
  data,
  columns,
  onRowClick,
  keyExtractor,
  loading = false,
  emptyMessage = 'No data'
}: TableProps<T>): React.ReactElement {
  if (loading) {
    return <div className="table-loading">Loading...</div>;
  }

  if (data.length === 0) {
    return <div className="table-empty">{emptyMessage}</div>;
  }

  return (
    <table>
      <thead>
        <tr>
          {columns.map(col => (
            <th key={String(col.key)}>{col.title}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {data.map(item => (
          <tr
            key={keyExtractor(item)}
            onClick={() => onRowClick?.(item)}
            style={{ cursor: onRowClick ? 'pointer' : 'default' }}
          >
            {columns.map(col => (
              <td key={String(col.key)}>
                {col.render
                  ? col.render(item[col.key], item)
                  : String(item[col.key])}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
```

---

## Custom Hooks

### Data Fetching Hook
```typescript
import { useState, useEffect, useCallback } from 'react';

interface UseApiResult<T> {
  data: T | null;
  loading: boolean;
  error: Error | null;
  refetch: () => Promise<void>;
}

interface UseApiOptions {
  immediate?: boolean;
}

/**
 * Hook for API data fetching with loading and error states.
 *
 * @template T - Expected response type
 * @param url - API endpoint
 * @param options - Hook configuration
 * @returns API state and refetch function
 *
 * @example
 * const { data, loading, error } = useApi<User[]>('/api/users');
 */
export function useApi<T>(
  url: string,
  options: UseApiOptions = {}
): UseApiResult<T> {
  const { immediate = true } = options;

  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const result = await response.json();
      setData(result);
    } catch (e) {
      setError(e instanceof Error ? e : new Error('Unknown error'));
    } finally {
      setLoading(false);
    }
  }, [url]);

  useEffect(() => {
    if (immediate) {
      fetchData();
    }
  }, [fetchData, immediate]);

  return { data, loading, error, refetch: fetchData };
}
```

### Form Hook
```typescript
import { useState, useCallback, ChangeEvent, FormEvent } from 'react';

interface UseFormResult<T> {
  values: T;
  errors: Partial<Record<keyof T, string>>;
  handleChange: (e: ChangeEvent<HTMLInputElement>) => void;
  handleSubmit: (e: FormEvent) => void;
  setFieldValue: (field: keyof T, value: T[keyof T]) => void;
  setFieldError: (field: keyof T, error: string) => void;
  reset: () => void;
  isValid: boolean;
}

/**
 * Hook for form state management with validation.
 *
 * @template T - Form values type
 * @param initialValues - Initial form values
 * @param onSubmit - Submit handler
 * @param validate - Validation function
 *
 * @example
 * const form = useForm(
 *   { email: '', password: '' },
 *   (values) => login(values),
 *   (values) => ({ email: !values.email ? 'Required' : undefined })
 * );
 */
export function useForm<T extends Record<string, unknown>>(
  initialValues: T,
  onSubmit: (values: T) => void | Promise<void>,
  validate?: (values: T) => Partial<Record<keyof T, string>>
): UseFormResult<T> {
  const [values, setValues] = useState<T>(initialValues);
  const [errors, setErrors] = useState<Partial<Record<keyof T, string>>>({});

  const handleChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setValues(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  }, []);

  const setFieldValue = useCallback((field: keyof T, value: T[keyof T]) => {
    setValues(prev => ({ ...prev, [field]: value }));
  }, []);

  const setFieldError = useCallback((field: keyof T, error: string) => {
    setErrors(prev => ({ ...prev, [field]: error }));
  }, []);

  const handleSubmit = useCallback(async (e: FormEvent) => {
    e.preventDefault();

    if (validate) {
      const validationErrors = validate(values);
      setErrors(validationErrors);

      if (Object.values(validationErrors).some(Boolean)) {
        return;
      }
    }

    await onSubmit(values);
  }, [values, validate, onSubmit]);

  const reset = useCallback(() => {
    setValues(initialValues);
    setErrors({});
  }, [initialValues]);

  const isValid = Object.values(errors).every(e => !e);

  return {
    values,
    errors,
    handleChange,
    handleSubmit,
    setFieldValue,
    setFieldError,
    reset,
    isValid
  };
}
```

---

## State Management

### Context Pattern
```typescript
import React, { createContext, useContext, useReducer, ReactNode } from 'react';

// State type
interface AuthState {
  user: User | null;
  loading: boolean;
  error: string | null;
}

// Action types
type AuthAction =
  | { type: 'LOGIN_START' }
  | { type: 'LOGIN_SUCCESS'; payload: User }
  | { type: 'LOGIN_FAILURE'; payload: string }
  | { type: 'LOGOUT' };

// Context type
interface AuthContextType extends AuthState {
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Reducer
function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'LOGIN_START':
      return { ...state, loading: true, error: null };
    case 'LOGIN_SUCCESS':
      return { ...state, loading: false, user: action.payload };
    case 'LOGIN_FAILURE':
      return { ...state, loading: false, error: action.payload };
    case 'LOGOUT':
      return { ...state, user: null };
    default:
      return state;
  }
}

// Provider
export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, {
    user: null,
    loading: false,
    error: null
  });

  const login = async (email: string, password: string) => {
    dispatch({ type: 'LOGIN_START' });
    try {
      const user = await authApi.login(email, password);
      dispatch({ type: 'LOGIN_SUCCESS', payload: user });
    } catch (error) {
      dispatch({
        type: 'LOGIN_FAILURE',
        payload: error instanceof Error ? error.message : 'Login failed'
      });
    }
  };

  const logout = () => {
    dispatch({ type: 'LOGOUT' });
  };

  return (
    <AuthContext.Provider value={{ ...state, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// Hook
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
```

---

## Testing Patterns

### Component Test
```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Counter } from './Counter';

describe('Counter', () => {
  it('should render with initial value', () => {
    render(<Counter initialValue={5} />);
    expect(screen.getByText('5')).toBeInTheDocument();
  });

  it('should increment when + button clicked', async () => {
    const user = userEvent.setup();
    render(<Counter initialValue={0} />);

    await user.click(screen.getByRole('button', { name: /increment/i }));

    expect(screen.getByText('1')).toBeInTheDocument();
  });

  it('should not exceed max value', async () => {
    const user = userEvent.setup();
    render(<Counter initialValue={9} max={10} />);

    await user.click(screen.getByRole('button', { name: /increment/i }));
    await user.click(screen.getByRole('button', { name: /increment/i }));

    expect(screen.getByText('10')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /increment/i })).toBeDisabled();
  });

  it('should call onChange when value changes', async () => {
    const handleChange = jest.fn();
    const user = userEvent.setup();
    render(<Counter onChange={handleChange} />);

    await user.click(screen.getByRole('button', { name: /increment/i }));

    expect(handleChange).toHaveBeenCalledWith(1);
  });
});
```

### Hook Test
```typescript
import { renderHook, waitFor } from '@testing-library/react';
import { useApi } from './useApi';

describe('useApi', () => {
  beforeEach(() => {
    global.fetch = jest.fn();
  });

  it('should fetch data successfully', async () => {
    const mockData = [{ id: 1, name: 'Test' }];
    (global.fetch as jest.Mock).mockResolvedValueOnce({
      ok: true,
      json: async () => mockData
    });

    const { result } = renderHook(() => useApi<typeof mockData>('/api/test'));

    expect(result.current.loading).toBe(true);

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.data).toEqual(mockData);
    expect(result.current.error).toBeNull();
  });

  it('should handle errors', async () => {
    (global.fetch as jest.Mock).mockRejectedValueOnce(new Error('Failed'));

    const { result } = renderHook(() => useApi('/api/test'));

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.error?.message).toBe('Failed');
    expect(result.current.data).toBeNull();
  });
});
```

---

## Prompt Templates for React

### Component Generation
```
// Create [ComponentName] component
// Props:
//   - prop1: type - description
//   - prop2?: type - optional description
// Features:
//   - [list features]
// Styling: [Tailwind/CSS Modules/Styled Components]
// Include: TypeScript, accessibility, tests
```

### Hook Generation
```
// Create use[HookName] hook
// Purpose: [description]
// Parameters:
//   - param1: type - description
// Returns:
//   - value1: type - description
//   - handler1: () => void - description
// Include: TypeScript, cleanup, error handling
```

### Context Generation
```
// Create [Name]Context
// State: [list state fields]
// Actions: [list actions]
// Include: TypeScript, reducer pattern, provider, hook
```

---

## Anti-Patterns to Avoid

### Don't Generate
- Class components (use functional)
- `any` type (use proper types)
- Inline functions in JSX without useCallback
- Missing key props in lists
- Direct DOM manipulation
- useEffect without dependency array

### Always Include
- TypeScript interfaces for all props
- Proper accessibility attributes (aria-*)
- Error boundaries for error handling
- Loading and error states
- Cleanup in useEffect
- Memoization for expensive operations
