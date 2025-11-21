# Python Copilot Instructions

## Hierarchy Level: Language-Specific

This file contains Python-specific rules for GitHub Copilot. These rules extend the root `.github/copilot-instructions.md`.

---

## Python Code Standards

### Type Hints (Required)
```python
# GOOD - Always include type hints
def process_user(user_id: int, data: dict[str, Any]) -> tuple[bool, str]:
    pass

# BAD - Missing type hints
def process_user(user_id, data):
    pass
```

### Docstring Format (Google Style)
```python
def calculate_price(
    base_price: float,
    discount: float,
    tax_rate: float = 0.1
) -> float:
    """
    Calculate final price with discount and tax.

    Args:
        base_price: The original price before discounts
        discount: Discount percentage (0.0 to 1.0)
        tax_rate: Tax rate to apply (default: 0.1)

    Returns:
        Final calculated price

    Raises:
        ValueError: If discount is negative or greater than 1

    Example:
        >>> calculate_price(100, 0.1, 0.1)
        99.0
    """
    pass
```

---

## Framework-Specific Rules

### FastAPI
```python
# Always include:
# - Pydantic models for request/response
# - Status codes
# - API documentation
# - Error handling

from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel, Field

class UserCreate(BaseModel):
    """Request model for creating user."""
    email: str = Field(..., description="User email address")
    name: str = Field(..., min_length=2, max_length=100)

    class Config:
        schema_extra = {
            "example": {
                "email": "user@example.com",
                "name": "John Doe"
            }
        }

@app.post("/users/", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
async def create_user(user: UserCreate):
    """Create a new user."""
    pass
```

### Flask
```python
# Always include:
# - Blueprints for organization
# - Error handlers
# - Request validation

from flask import Blueprint, jsonify, request
from marshmallow import Schema, fields, validate

user_bp = Blueprint('users', __name__)

class UserSchema(Schema):
    email = fields.Email(required=True)
    name = fields.Str(required=True, validate=validate.Length(min=2, max=100))

@user_bp.route('/users/', methods=['POST'])
def create_user():
    """Create a new user."""
    pass
```

### Data Science (Pandas/NumPy)
```python
# Always include:
# - Clear variable names
# - Method chaining where appropriate
# - Documentation for transformations

import pandas as pd
import numpy as np

def clean_data(df: pd.DataFrame) -> pd.DataFrame:
    """
    Clean and preprocess the dataset.

    Steps:
    1. Remove duplicates
    2. Handle missing values
    3. Standardize column names
    """
    return (df
        .drop_duplicates()
        .dropna(subset=['required_column'])
        .rename(columns=str.lower))
```

---

## Testing Requirements

### pytest Structure
```python
# tests/test_user_service.py

import pytest
from unittest.mock import Mock, patch

class TestUserService:
    """Test suite for UserService."""

    @pytest.fixture
    def user_service(self):
        """Provide UserService instance."""
        return UserService()

    @pytest.fixture
    def sample_user(self):
        """Provide sample user data."""
        return {"email": "test@example.com", "name": "Test User"}

    def test_create_user_success(self, user_service, sample_user):
        """Should create user with valid data."""
        # Arrange
        # Act
        # Assert
        pass

    def test_create_user_invalid_email(self, user_service):
        """Should raise ValueError for invalid email."""
        with pytest.raises(ValueError, match="Invalid email"):
            pass
```

---

## Common Patterns

### Dataclass with Validation
```python
from dataclasses import dataclass, field
from typing import Optional
import re

@dataclass
class User:
    """User entity with built-in validation."""
    id: int
    email: str
    name: str
    age: Optional[int] = None

    def __post_init__(self):
        """Validate fields after initialization."""
        if not re.match(r'^[\w\.-]+@[\w\.-]+\.\w+$', self.email):
            raise ValueError(f"Invalid email: {self.email}")
        if self.age is not None and (self.age < 0 or self.age > 150):
            raise ValueError(f"Invalid age: {self.age}")
```

### Repository Pattern
```python
from abc import ABC, abstractmethod
from typing import Generic, TypeVar, Optional, List

T = TypeVar('T')

class Repository(ABC, Generic[T]):
    """Abstract repository for CRUD operations."""

    @abstractmethod
    def find_by_id(self, id: int) -> Optional[T]:
        pass

    @abstractmethod
    def find_all(self) -> List[T]:
        pass

    @abstractmethod
    def save(self, entity: T) -> T:
        pass

    @abstractmethod
    def delete(self, id: int) -> bool:
        pass
```

### Context Manager
```python
from contextlib import contextmanager
from typing import Generator

@contextmanager
def database_transaction() -> Generator[Connection, None, None]:
    """
    Context manager for database transactions.

    Automatically commits on success, rolls back on error.
    """
    connection = get_connection()
    try:
        yield connection
        connection.commit()
    except Exception:
        connection.rollback()
        raise
    finally:
        connection.close()
```

---

## Prompt Templates for Python

### Function Generation
```
# Function to [action] with [specific requirements]
# Parameters:
#   - param1: [type] - [description]
#   - param2: [type] - [description]
# Returns: [type] - [description]
# Raises: [ExceptionType] when [condition]
# Example: [usage example]
```

### Class Generation
```
# Class for [purpose]
#
# Attributes:
#   - attr1: [type] - [description]
#
# Methods:
#   - method1(params) -> return_type: [description]
#
# Implements: [pattern/interface]
```

### Test Generation
```
# Test [function/class name]
#
# Scenarios:
#   - Should [expected behavior] when [condition]
#   - Should raise [Exception] when [condition]
#
# Fixtures needed: [list]
```

---

## Anti-Patterns to Avoid

### Don't Generate
- Bare `except:` clauses (always specify exception type)
- Mutable default arguments
- Global variables for state
- Magic numbers without constants
- print() statements for logging (use logging module)

### Always Include
- Type hints on all functions
- Docstrings on all public functions
- Error handling for external calls
- Input validation
- Resource cleanup (context managers)
