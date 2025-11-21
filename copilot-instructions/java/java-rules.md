# Java Copilot Instructions

## Hierarchy Level: Language-Specific

This file contains Java-specific rules for GitHub Copilot. These rules extend the root `.github/copilot-instructions.md`.

---

## Java Code Standards

### JavaDoc Format (Required)
```java
/**
 * Calculate the final price with discount and tax.
 *
 * <p>This method applies the discount first, then calculates tax on the
 * discounted price.</p>
 *
 * @param basePrice the original price before discounts
 * @param discount discount percentage (0.0 to 1.0)
 * @param taxRate tax rate to apply
 * @return the final calculated price
 * @throws IllegalArgumentException if discount is negative or greater than 1
 * @since 1.0
 *
 * @example
 * <pre>{@code
 * double price = calculatePrice(100.0, 0.1, 0.1);
 * // price = 99.0
 * }</pre>
 */
public double calculatePrice(double basePrice, double discount, double taxRate) {
    // Implementation
}
```

### Class Structure
```java
/**
 * Service for managing user operations.
 *
 * @author Generated with Copilot
 * @version 1.0
 * @since 2025-01-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    // Constants first
    private static final int MAX_RETRY_ATTEMPTS = 3;

    // Dependencies (final fields)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Public methods
    public UserResponse createUser(UserCreateRequest request) {
        // Implementation
    }

    // Private helper methods at the bottom
    private void validateEmail(String email) {
        // Implementation
    }
}
```

---

## Spring Boot Rules

### REST Controller Pattern
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for user operations")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "User exists")
    })
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
```

### Service Layer Pattern
```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating user: {}", request.getEmail());

        // Check for duplicate
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Create and save
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        log.info("User created with ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toResponse)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

### Repository Pattern
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE created_at > :date",
           nativeQuery = true)
    List<User> findRecentUsers(@Param("date") LocalDateTime date);
}
```

### DTO Pattern with Validation
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain uppercase, lowercase, and number"
    )
    private String password;

    @Min(value = 18, message = "Must be at least 18 years old")
    @Max(value = 120, message = "Age must be realistic")
    private Integer age;
}
```

---

## Design Patterns

### Builder Pattern
```java
public final class User {
    private final Long id;
    private final String email;
    private final String name;
    private final LocalDateTime createdAt;

    private User(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
    }

    // Getters only (immutable)
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String email;
        private String name;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public User build() {
            Objects.requireNonNull(email, "Email is required");
            Objects.requireNonNull(name, "Name is required");
            return new User(this);
        }
    }
}
```

### Factory Pattern
```java
public interface NotificationSender {
    void send(String recipient, String message);
}

@Component
public class NotificationFactory {

    private final Map<NotificationType, NotificationSender> senders;

    public NotificationFactory(List<NotificationSender> senderList) {
        this.senders = senderList.stream()
            .collect(Collectors.toMap(
                sender -> sender.getType(),
                Function.identity()
            ));
    }

    public NotificationSender getSender(NotificationType type) {
        NotificationSender sender = senders.get(type);
        if (sender == null) {
            throw new UnsupportedOperationException(
                "No sender for type: " + type
            );
        }
        return sender;
    }
}
```

---

## Testing Requirements

### JUnit 5 Structure
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserCreateRequest createRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        createRequest = UserCreateRequest.builder()
            .email("test@example.com")
            .name("Test User")
            .build();

        user = User.builder()
            .id(1L)
            .email("test@example.com")
            .name("Test User")
            .build();

        userResponse = UserResponse.builder()
            .id(1L)
            .email("test@example.com")
            .name("Test User")
            .build();
    }

    @Nested
    @DisplayName("Create User")
    class CreateUser {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            // Given
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userMapper.toEntity(any())).thenReturn(user);
            when(userRepository.save(any())).thenReturn(user);
            when(userMapper.toResponse(any())).thenReturn(userResponse);

            // When
            UserResponse result = userService.createUser(createRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception for duplicate email")
        void shouldThrowExceptionForDuplicateEmail() {
            // Given
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            // When & Then
            assertThrows(DuplicateEmailException.class,
                () -> userService.createUser(createRequest));
            verify(userRepository, never()).save(any());
        }
    }
}
```

### Integration Test
```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("User API Integration Tests")
class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create user via API")
    void shouldCreateUser() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
            .email("test@example.com")
            .name("Test User")
            .password("Password123")
            .build();

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"));
    }
}
```

---

## Prompt Templates for Java

### REST Controller
```
// Create REST controller for [Entity]
// Endpoints: GET all, GET by id, POST, PUT, DELETE
// Include: validation, error handling, Swagger docs
// Security: [authentication requirements]
```

### Service Layer
```
// Create service for [Entity]
// Operations: [list operations]
// Includes: transactions, caching, logging
// Dependencies: [repositories, mappers]
```

### Entity Class
```
// Create JPA entity for [Table]
// Fields: [field list with types]
// Relationships: [OneToMany, ManyToOne, etc.]
// Includes: auditing, soft delete
```

---

## Anti-Patterns to Avoid

### Don't Generate
- Field injection (`@Autowired` on fields) - use constructor injection
- Checked exceptions for business logic - use runtime exceptions
- Anemic domain models - include business logic in entities
- Raw SQL without parameters - always use parameterized queries
- Synchronous calls for external services - use async where possible

### Always Include
- Lombok annotations for boilerplate reduction
- Proper exception handling with custom exceptions
- Logging at appropriate levels
- Transaction boundaries
- Input validation with Bean Validation
