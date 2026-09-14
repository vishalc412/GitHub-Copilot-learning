import type { ApiError } from '../api/client';

interface ErrorBannerProps {
  error: ApiError | null;
  onDismiss?: () => void;
  onRetry?: () => void;
}

/**
 * Renders an {@link ApiError} as an actionable message.
 *
 * Distinguishes the case the user can actually do something about — the
 * backend is not running — from a server-side rejection, and offers retry
 * only where retrying makes sense. Field-level validation errors are shown by
 * the form next to the inputs, not here.
 */
export function ErrorBanner({ error, onDismiss, onRetry }: ErrorBannerProps) {
  if (error === null) {
    return null;
  }

  const isValidation = Object.keys(error.fieldErrors).length > 0;
  if (isValidation) {
    // The form is already showing these inline; a banner would be noise.
    return null;
  }

  return (
    <div className="error-banner" role="alert">
      <div className="error-banner-body">
        <strong>{error.isNetworkError ? 'Cannot reach the API' : 'Request failed'}</strong>
        <p>
          {error.message}
          {error.isNetworkError && (
            <>
              {' '}
              Start it with <code>mvn spring-boot:run</code> in{' '}
              <code>backend/</code>.
            </>
          )}
        </p>
      </div>

      <div className="error-banner-actions">
        {onRetry && (
          <button type="button" className="btn btn-sm" onClick={onRetry}>
            Retry
          </button>
        )}
        {onDismiss && (
          <button type="button" className="btn btn-sm btn-ghost" onClick={onDismiss}>
            Dismiss
          </button>
        )}
      </div>
    </div>
  );
}
