# GitHub Copilot Mastery Rules

## Hierarchy Level: Core Rules

These are the fundamental rules for effective GitHub Copilot usage. Master these before moving to language-specific guides.

---

## Rule 1: Context is King

### The Principle
Copilot generates code based on context. More context = better suggestions.

### How to Apply

**DO provide context through:**
1. Open related files in your IDE
2. Write clear file names
3. Add import statements first
4. Define types/interfaces before using them
5. Write descriptive function names

**DON'T:**
1. Work in isolated files
2. Use generic names like `data`, `temp`, `x`
3. Skip type definitions
4. Close related files

### Exercise 1.1: Context Comparison
```python
# BAD CONTEXT - Try this first
def process(data):
    pass

# GOOD CONTEXT - Try this second
from typing import Dict, List, Optional
from dataclasses import dataclass

@dataclass
class Customer:
    id: int
    name: str
    email: str
    orders: List['Order']

def process_customer_data(
    customer: Customer,
    include_orders: bool = True
) -> Dict[str, any]:
    """Transform customer data for API response."""
    pass
```

**Assignment 1**: Create both versions and compare what Copilot generates. Document the differences.

---

## Rule 2: Comments Before Code

### The Principle
Write descriptive comments BEFORE the code. Copilot reads comments as instructions.

### The Comment Formula
```
[Action verb] [object] [with/by/using] [method/constraint]
```

### Examples

**Level 1: Basic Comment**
```python
# Calculate total
def calc():
    pass
```

**Level 2: Better Comment**
```python
# Calculate the total price of items in cart
def calculate_cart_total():
    pass
```

**Level 3: Best Comment**
```python
# Calculate the total price of items in cart
# Apply discount code if provided
# Add tax based on shipping address state
# Return breakdown: subtotal, discount, tax, total
def calculate_cart_total(
    cart: Cart,
    discount_code: Optional[str] = None,
    shipping_state: str = "CA"
) -> PriceBreakdown:
    pass
```

### Exercise 2.1: Progressive Comments
Create these functions with increasingly detailed comments:

```python
# Basic: Function to send email
# Better: Function to send welcome email to new user
# Best: Function to send welcome email to new user with:
#       - Personalized greeting using first name
#       - Account verification link
#       - Retry 3 times on failure
#       - Log each attempt
```

**Assignment 2**: Write 5 functions using the three-level comment progression.

---

## Rule 3: Type Everything

### The Principle
Type hints dramatically improve Copilot suggestions.

### Python Type Hints
```python
# Without types - Copilot guesses
def process(items, filter_fn, limit):
    pass

# With types - Copilot knows exactly what to do
def process(
    items: List[Product],
    filter_fn: Callable[[Product], bool],
    limit: int = 10
) -> List[Product]:
    pass
```

### Java Types
```java
// Be explicit with generics
public List<OrderDTO> getOrders(
    Long customerId,
    LocalDate startDate,
    LocalDate endDate,
    Pageable pageable
) {
    // Copilot generates proper implementation
}
```

### TypeScript Types
```typescript
// Define interfaces first
interface FilterOptions<T> {
  predicate: (item: T) => boolean;
  limit?: number;
  sortBy?: keyof T;
}

function filterItems<T>(
  items: T[],
  options: FilterOptions<T>
): T[] {
  // Copilot generates type-safe implementation
}
```

### Exercise 3.1: Type Inference
Convert these untyped functions to fully typed versions:

```python
# Convert this
def get_users(active, limit):
    pass

def process_order(order, payment):
    pass

def send_notification(user, message, channel):
    pass
```

**Assignment 3**: Add comprehensive types to 10 functions in your existing codebase.

---

## Rule 4: Use Design Patterns

### The Principle
Copilot recognizes common patterns and generates consistent code.

### Pattern Keywords That Copilot Recognizes

1. **Repository** - Data access layer
2. **Service** - Business logic layer
3. **Factory** - Object creation
4. **Builder** - Complex object construction
5. **Strategy** - Interchangeable algorithms
6. **Observer** - Event handling
7. **Decorator** - Adding behavior
8. **Singleton** - Single instance

### Example: Pattern Recognition
```python
# Copilot recognizes "Repository" pattern
class UserRepository:
    def __init__(self, db):
        self.db = db

    def find_by_id(self, id: int) -> Optional[User]:
        # Copilot generates DB query

    def find_all(self) -> List[User]:
        # Copilot generates list query

    def save(self, user: User) -> User:
        # Copilot generates save logic

    def delete(self, id: int) -> bool:
        # Copilot generates delete logic
```

### Exercise 4.1: Pattern Implementation
Implement these patterns and observe Copilot's suggestions:

```python
# 1. Builder pattern
class EmailBuilder:
    # Let Copilot generate the builder

# 2. Strategy pattern
class PaymentStrategy:
    # Let Copilot generate the strategy interface

# 3. Factory pattern
class NotificationFactory:
    # Let Copilot generate the factory
```

**Assignment 4**: Implement all 8 design patterns listed above in your preferred language.

---

## Rule 5: Test-Driven Development

### The Principle
Writing tests first gives Copilot a specification to implement against.

### TDD with Copilot Workflow

```python
# Step 1: Write the test first
def test_calculate_discount():
    # Test 10% discount
    assert calculate_discount(100, 0.10) == 90.0

    # Test 0% discount
    assert calculate_discount(100, 0.00) == 100.0

    # Test 100% discount
    assert calculate_discount(100, 1.00) == 0.0

    # Test with float price
    assert calculate_discount(99.99, 0.15) == pytest.approx(84.99, 0.01)

def test_calculate_discount_errors():
    # Negative price
    with pytest.raises(ValueError):
        calculate_discount(-100, 0.10)

    # Negative discount
    with pytest.raises(ValueError):
        calculate_discount(100, -0.10)

    # Discount > 100%
    with pytest.raises(ValueError):
        calculate_discount(100, 1.50)

# Step 2: Now implement - Copilot knows exactly what's needed
def calculate_discount(price: float, discount_rate: float) -> float:
    """
    Calculate discounted price.

    Args:
        price: Original price (must be >= 0)
        discount_rate: Discount as decimal (0.0 to 1.0)

    Returns:
        Discounted price

    Raises:
        ValueError: If price negative or discount out of range
    """
    # Copilot generates implementation matching tests
```

### Exercise 5.1: TDD Practice
Write tests first, then implementation:

```python
# 1. Write tests for a password validator
def test_password_validator():
    # At least 8 characters
    # Contains uppercase
    # Contains lowercase
    # Contains number
    # Contains special character

# 2. Write tests for an email parser
def test_email_parser():
    # Valid email
    # Invalid email
    # Extract username
    # Extract domain

# 3. Write tests for a pagination function
def test_paginate():
    # First page
    # Last page
    # Out of range
    # Empty list
```

**Assignment 5**: Create a complete module using TDD - write all tests before any implementation.

---

## Rule 6: Incremental Complexity

### The Principle
Start simple, add complexity incrementally. Copilot builds on existing code.

### Workflow

```python
# Phase 1: Basic function
def get_user(user_id: int) -> User:
    """Get user by ID."""
    # Simple implementation

# Phase 2: Add error handling
def get_user(user_id: int) -> User:
    """
    Get user by ID.

    Raises:
        UserNotFoundError: If user doesn't exist
    """
    # With error handling

# Phase 3: Add caching
@cache(ttl=300)
def get_user(user_id: int) -> User:
    """
    Get user by ID with caching.

    Cache: 5 minutes TTL
    Raises:
        UserNotFoundError: If user doesn't exist
    """
    # With caching

# Phase 4: Add logging
@cache(ttl=300)
def get_user(user_id: int) -> User:
    """
    Get user by ID with caching and logging.

    Logs:
        - INFO: User fetch attempt
        - DEBUG: Cache hit/miss
        - ERROR: User not found
    """
    # With logging
```

### Exercise 6.1: Progressive Enhancement
Start with this basic function and add features incrementally:

```python
# Start
def send_message(to: str, message: str) -> bool:
    pass

# Add: Retry logic
# Add: Rate limiting
# Add: Template support
# Add: Attachments
# Add: Scheduling
# Add: Analytics tracking
```

**Assignment 6**: Take 3 simple functions and progressively add 5 features to each.

---

## Rule 7: Use Copilot Chat Effectively

### Commands to Know

| Command | Purpose | Example |
|---------|---------|---------|
| /explain | Understand code | "Explain this function" |
| /fix | Fix bugs | "Fix the null pointer issue" |
| /tests | Generate tests | "Create unit tests" |
| /doc | Add documentation | "Add JSDoc comments" |
| /optimize | Improve performance | "Optimize this query" |

### Effective Questions

**Bad Questions:**
- "Fix this"
- "Make it work"
- "What's wrong?"

**Good Questions:**
- "Why does this function return None instead of an empty list?"
- "How can I optimize this database query that's timing out?"
- "Generate unit tests that cover edge cases for this validation function"

### Exercise 7.1: Chat Practice
Use Copilot Chat for these tasks:

1. Ask it to explain a complex regex
2. Ask it to refactor a function to be more testable
3. Ask it to add error handling to existing code
4. Ask it to generate test cases
5. Ask it to optimize a slow function

**Assignment 7**: Document 10 effective chat prompts that worked well for you.

---

## Rule 8: Keyboard Shortcuts Mastery

### Essential Shortcuts

| Action | Windows/Linux | Mac |
|--------|---------------|-----|
| Accept suggestion | Tab | Tab |
| Reject | Esc | Esc |
| Next suggestion | Alt + ] | Option + ] |
| Previous | Alt + [ | Option + [ |
| Trigger inline | Ctrl + Enter | Cmd + Enter |
| Open Chat | Ctrl + I | Cmd + I |

### Exercise 8.1: Speed Drill
Practice these tasks using only shortcuts (no mouse):

1. Generate a function from comment
2. Cycle through 5 suggestions
3. Accept partial suggestion
4. Open chat and ask question
5. Apply chat suggestion

**Assignment 8**: Create a personal cheat sheet with your most-used shortcuts.

---

## Rule 9: Review Before Accept

### The Checklist

Before accepting any Copilot suggestion, verify:

- [ ] **Correctness**: Does it do what you intended?
- [ ] **Types**: Are types correct and complete?
- [ ] **Edge Cases**: Does it handle null/empty/error cases?
- [ ] **Security**: No SQL injection, XSS, etc.?
- [ ] **Performance**: No obvious inefficiencies?
- [ ] **Style**: Matches project conventions?

### Common Issues to Watch For

1. **Hallucinated APIs**: Copilot may suggest APIs that don't exist
2. **Outdated Syntax**: May use deprecated methods
3. **Security Holes**: May not sanitize inputs
4. **Off-by-One Errors**: Common in loops and pagination
5. **Null Handling**: May forget null checks

### Exercise 9.1: Code Review
Review this Copilot-generated code for issues:

```python
def get_user_orders(user_id):
    query = f"SELECT * FROM orders WHERE user_id = {user_id}"
    results = db.execute(query)
    return [Order(r) for r in results]
```

**Assignment 9**: Review 20 Copilot suggestions and document issues found.

---

## Rule 10: Continuous Learning

### The Principle
Copilot improves with your project. Keep your codebase clean and consistent.

### Best Practices

1. **Maintain consistent style** - Copilot learns from your code
2. **Keep good examples nearby** - Open reference files
3. **Update types and docs** - Better context = better suggestions
4. **Refactor regularly** - Clean code generates clean code
5. **Track what works** - Build your own prompt library

### Exercise 10.1: Personal Prompt Library
Create templates for:

1. REST endpoint
2. Database query
3. Unit test
4. Data transformation
5. Error handler

**Assignment 10**: Build a personal library of 20+ effective prompts.

---

## Mastery Certification Checklist

Complete these to prove mastery:

- [ ] Generate 100+ functions using comments
- [ ] Implement all 8 design patterns
- [ ] Complete 10 TDD cycles
- [ ] Use all Chat commands effectively
- [ ] Build personal prompt library (20+ prompts)
- [ ] Review and fix 50+ Copilot suggestions
- [ ] Achieve 60%+ code generation rate
- [ ] Train a teammate on Copilot

---

## Quick Reference Card

```
+----------------------------------+
|    COPILOT SUCCESS FORMULA       |
+----------------------------------+
| 1. Open related files (context)  |
| 2. Write types first             |
| 3. Add descriptive comment       |
| 4. Start function signature      |
| 5. Let Copilot suggest           |
| 6. Review before accepting       |
| 7. Iterate and improve           |
+----------------------------------+
```
