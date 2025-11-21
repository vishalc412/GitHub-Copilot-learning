# Copilot Prompt Patterns

## Hierarchy Level: Cross-Language Patterns

This file contains effective prompt patterns for GitHub Copilot that work across all languages.

---

## The Hierarchy of Effective Prompts

```
Level 1: Context (Files, Imports, Types)
    ↓
Level 2: Intent (Comments, Docstrings)
    ↓
Level 3: Specificity (Parameters, Examples)
    ↓
Level 4: Constraints (Edge Cases, Validation)
```

---

## Pattern 1: Signature-First Pattern

**Concept**: Define the complete function signature before implementation.

### Python
```python
def calculate_compound_interest(
    principal: float,
    rate: float,
    time: int,
    n: int = 12
) -> float:
    """Calculate compound interest."""
    # Copilot generates implementation
```

### Java
```java
public BigDecimal calculateCompoundInterest(
    BigDecimal principal,
    BigDecimal rate,
    int time,
    int compoundingFrequency
) {
    // Copilot generates implementation
}
```

### TypeScript
```typescript
function calculateCompoundInterest(
  principal: number,
  rate: number,
  time: number,
  n: number = 12
): number {
  // Copilot generates implementation
}
```

---

## Pattern 2: Example-Driven Pattern

**Concept**: Provide concrete examples of input/output behavior.

### Template
```
/**
 * [Description]
 *
 * @example
 * Input: [example input]
 * Output: [expected output]
 *
 * @example
 * Input: [edge case input]
 * Output: [expected output]
 */
```

### Python
```python
def parse_date(date_string: str) -> datetime:
    """
    Parse date string into datetime object.

    Examples:
        >>> parse_date("2024-01-15")
        datetime(2024, 1, 15)

        >>> parse_date("01/15/2024")
        datetime(2024, 1, 15)

        >>> parse_date("January 15, 2024")
        datetime(2024, 1, 15)
    """
    # Copilot generates multi-format parser
```

### Java
```java
/**
 * Parse date string into LocalDate.
 *
 * <pre>{@code
 * parseDate("2024-01-15")     -> LocalDate.of(2024, 1, 15)
 * parseDate("01/15/2024")     -> LocalDate.of(2024, 1, 15)
 * parseDate("January 15, 2024") -> LocalDate.of(2024, 1, 15)
 * }</pre>
 */
public LocalDate parseDate(String dateString) {
    // Copilot generates implementation
}
```

---

## Pattern 3: Step-by-Step Pattern

**Concept**: Break complex logic into numbered steps.

### Template
```
// Step 1: [First action]
// Step 2: [Second action]
// Step 3: [Third action]
// Step 4: [Return/Final action]
```

### Python
```python
def process_order(order: Order) -> ProcessedOrder:
    """
    Process an order through the fulfillment pipeline.

    Steps:
    1. Validate order items and quantities
    2. Check inventory availability
    3. Calculate total with discounts and taxes
    4. Reserve inventory
    5. Create payment intent
    6. Generate confirmation
    """
    # Step 1: Validate order items and quantities

    # Step 2: Check inventory availability

    # Step 3: Calculate total with discounts and taxes

    # Step 4: Reserve inventory

    # Step 5: Create payment intent

    # Step 6: Generate confirmation
```

### Java
```java
public ProcessedOrder processOrder(Order order) {
    // Step 1: Validate order items and quantities

    // Step 2: Check inventory availability

    // Step 3: Calculate total with discounts and taxes

    // Step 4: Reserve inventory

    // Step 5: Create payment intent

    // Step 6: Generate confirmation
}
```

---

## Pattern 4: Contract Pattern

**Concept**: Define pre-conditions, post-conditions, and invariants.

### Template
```
/**
 * [Description]
 *
 * Pre-conditions:
 * - [condition 1]
 * - [condition 2]
 *
 * Post-conditions:
 * - [condition 1]
 * - [condition 2]
 *
 * Invariants:
 * - [invariant 1]
 */
```

### Python
```python
def transfer_funds(
    from_account: Account,
    to_account: Account,
    amount: Decimal
) -> TransferResult:
    """
    Transfer funds between accounts.

    Pre-conditions:
    - from_account.balance >= amount
    - amount > 0
    - from_account != to_account

    Post-conditions:
    - from_account.balance decreased by amount
    - to_account.balance increased by amount
    - Total balance unchanged (from + to = original)

    Invariants:
    - No account has negative balance
    - Transaction is atomic (all or nothing)
    """
    # Copilot generates with all validations
```

---

## Pattern 5: Test-First Pattern

**Concept**: Write tests first, then implementation.

### Python
```python
# Write tests first
def test_calculate_discount():
    assert calculate_discount(100, 0.1) == 90.0
    assert calculate_discount(100, 0.0) == 100.0
    assert calculate_discount(100, 1.0) == 0.0
    assert calculate_discount(0, 0.5) == 0.0

def test_calculate_discount_invalid():
    with pytest.raises(ValueError):
        calculate_discount(-100, 0.1)
    with pytest.raises(ValueError):
        calculate_discount(100, -0.1)
    with pytest.raises(ValueError):
        calculate_discount(100, 1.5)

# Now implement - Copilot knows the requirements
def calculate_discount(price: float, discount: float) -> float:
    """Calculate discounted price."""
    # Copilot generates implementation matching tests
```

---

## Pattern 6: Similar-To Pattern

**Concept**: Reference existing code for consistency.

### Template
```
// Similar to [existing_function/class]
// But with [differences]
```

### Example
```python
# Existing code
class UserRepository:
    def find_by_id(self, id: int) -> Optional[User]:
        return self.db.query(User).filter(User.id == id).first()

    def find_all(self) -> List[User]:
        return self.db.query(User).all()

# New code - Copilot follows existing pattern
# Similar to UserRepository but for Product entity
class ProductRepository:
    # Copilot generates matching methods
```

---

## Pattern 7: Boundary Pattern

**Concept**: Define limits and edge cases explicitly.

### Template
```
/**
 * [Description]
 *
 * Boundaries:
 * - Minimum: [value] -> [behavior]
 * - Maximum: [value] -> [behavior]
 * - Empty: [behavior]
 * - Null/None: [behavior]
 */
```

### Python
```python
def paginate(
    items: List[T],
    page: int,
    page_size: int
) -> PaginatedResult[T]:
    """
    Paginate a list of items.

    Boundaries:
    - page < 1: Return first page
    - page > max_pages: Return empty list
    - page_size < 1: Use default (10)
    - page_size > 100: Cap at 100
    - items empty: Return empty result with total=0

    Args:
        items: List to paginate
        page: 1-indexed page number
        page_size: Items per page

    Returns:
        PaginatedResult with items, page, total_pages, total_items
    """
    # Copilot generates with all boundary handling
```

---

## Pattern 8: Error-First Pattern

**Concept**: Define error conditions before happy path.

### Template
```
/**
 * [Description]
 *
 * Errors:
 * - [ErrorType1]: when [condition]
 * - [ErrorType2]: when [condition]
 *
 * Success:
 * - Returns [type] when [condition]
 */
```

### Java
```java
/**
 * Authenticate user with credentials.
 *
 * Errors:
 * - UserNotFoundException: email not registered
 * - InvalidPasswordException: password doesn't match
 * - AccountLockedException: too many failed attempts
 * - AccountDisabledException: account is disabled
 *
 * Success:
 * - Returns AuthToken when credentials valid
 */
public AuthToken authenticate(String email, String password) {
    // Copilot generates with all error handling
}
```

---

## Pattern 9: Configuration Pattern

**Concept**: Define configurable behavior through options.

### TypeScript
```typescript
interface SearchOptions {
  /** Search in these fields only */
  fields?: string[];
  /** Case-sensitive search */
  caseSensitive?: boolean;
  /** Maximum results to return */
  limit?: number;
  /** Offset for pagination */
  offset?: number;
  /** Sort by field */
  sortBy?: string;
  /** Sort direction */
  sortOrder?: 'asc' | 'desc';
  /** Include partial matches */
  fuzzy?: boolean;
}

/**
 * Search items with configurable options.
 *
 * Default behavior:
 * - Searches all string fields
 * - Case-insensitive
 * - Returns max 100 results
 * - No offset
 * - No sorting
 * - Exact matches only
 */
function search<T>(
  items: T[],
  query: string,
  options: SearchOptions = {}
): T[] {
  // Copilot generates flexible search
}
```

---

## Pattern 10: Transformation Pattern

**Concept**: Define input and output shapes for data transformation.

### Template
```
/**
 * Transform [input type] to [output type]
 *
 * Input shape:
 * {
 *   field1: type,
 *   field2: type
 * }
 *
 * Output shape:
 * {
 *   newField1: type (from field1),
 *   newField2: type (computed)
 * }
 */
```

### Python
```python
def transform_user_response(raw_data: dict) -> UserDTO:
    """
    Transform API response to UserDTO.

    Input shape (from API):
    {
        "user_id": int,
        "first_name": str,
        "last_name": str,
        "email_address": str,
        "created_at": str (ISO format),
        "roles": [{"id": int, "name": str}]
    }

    Output shape (UserDTO):
    {
        "id": int (from user_id),
        "fullName": str (first_name + last_name),
        "email": str (from email_address),
        "createdAt": datetime (parsed),
        "roleNames": List[str] (extracted from roles)
    }
    """
    # Copilot generates transformation
```

---

## Combining Patterns

### Complex Function Example

```python
def process_payment(
    order_id: str,
    payment_method: PaymentMethod,
    amount: Decimal,
    currency: str = "USD",
    idempotency_key: Optional[str] = None
) -> PaymentResult:
    """
    Process payment for an order.

    Pre-conditions:
    - order_id exists and is not already paid
    - payment_method is valid and not expired
    - amount > 0

    Steps:
    1. Validate order and payment method
    2. Check for duplicate (using idempotency_key)
    3. Create payment intent
    4. Attempt charge
    5. Update order status
    6. Send confirmation

    Errors:
    - OrderNotFoundError: order doesn't exist
    - OrderAlreadyPaidError: order was already paid
    - InvalidPaymentMethodError: payment method invalid
    - PaymentDeclinedError: charge was declined
    - InsufficientFundsError: not enough balance

    Post-conditions:
    - Order marked as paid on success
    - Payment record created
    - Confirmation sent to customer

    Examples:
        >>> process_payment("ORD-123", card, Decimal("99.99"))
        PaymentResult(success=True, transaction_id="TXN-456")

        >>> process_payment("ORD-123", expired_card, Decimal("99.99"))
        raises InvalidPaymentMethodError
    """
    # Copilot generates comprehensive implementation
```

---

## Anti-Patterns

### Avoid These Prompts

```python
# TOO VAGUE
# Function to process data
def process(data):  # What data? What processing?

# TOO LONG WITHOUT STRUCTURE
# This function should take user data from the API response and
# validate it and then transform it to our internal format and
# save it to the database and send an email and return the result
def handle_user(data):  # Break this down!

# NO TYPES
def calculate(a, b, c):  # What types? What calculation?

# IMPLEMENTATION DETAILS IN COMMENTS
# Use a for loop to iterate through the list and check each item
def find_item(items):  # Describe WHAT, not HOW
```

### Better Versions

```python
# SPECIFIC AND TYPED
def process_user_registration(data: RegistrationData) -> User:
    """Process and validate user registration data."""

# BROKEN INTO STEPS
def handle_user(api_response: dict) -> HandleUserResult:
    """
    Handle user data from API.

    Steps:
    1. Validate required fields
    2. Transform to User entity
    3. Save to database
    4. Send welcome email
    """

# WITH TYPES AND PURPOSE
def calculate_compound_interest(
    principal: float,
    rate: float,
    periods: int
) -> float:
    """Calculate compound interest over given periods."""

# DESCRIBES WHAT, NOT HOW
def find_user_by_email(
    users: List[User],
    email: str
) -> Optional[User]:
    """Find user with matching email, case-insensitive."""
```

---

## Quick Reference Card

| Pattern | When to Use | Key Elements |
|---------|-------------|--------------|
| Signature-First | Any function | Types, names, defaults |
| Example-Driven | Complex transformations | Input/output examples |
| Step-by-Step | Multi-stage processes | Numbered steps |
| Contract | Critical business logic | Pre/post conditions |
| Test-First | TDD workflow | Test cases first |
| Similar-To | Maintaining consistency | Reference to existing |
| Boundary | Edge case handling | Min/max/empty/null |
| Error-First | Error-prone operations | Error conditions |
| Configuration | Flexible functions | Options interface |
| Transformation | Data mapping | Input/output shapes |
