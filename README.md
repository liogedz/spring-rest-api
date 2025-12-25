# JAVA SPRINGBOOT CRUD REST API

A comprehensive REST API demonstrating Spring Boot best practices, authentication, and CRUD operations.

## Features

- 🔐 JWT-based authentication with 2FA (email verification)
- 📧 Email service integration (Gmail)
- 👤 User management with role-based access control (USER/ADMIN)
- ✅ Full CRUD operations (GET, POST, PUT, PATCH, DELETE)
- 🛡️ Global exception handling
- 📝 Bean validation
- 🗄️ H2 in-memory database
- 🎯 Clean architecture (Controller → Service → Repository)

## Tech Stack

- Java 21
- Spring Boot 4.x
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (JSON Web Tokens)
- Maven
- JavaMail API

## API Endpoints

### Authentication

- `POST /api/v1/auth/signup` - Register new user
- `POST /api/v1/auth/login` - Login (triggers 2FA email)
- `POST /api/v1/auth/verify` - Verify code and receive JWT token

### Users (Protected)

- `GET /api/v1/users` - Get all users (ADMIN only)
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users/current-user` - Get current user
- `PUT /api/v1/users/{id}` - Full update (own account or ADMIN)
- `PATCH /api/v1/users/{id}` - Partial update (own account or ADMIN)
- `DELETE /api/v1/users/{id}` - Delete user (own account or ADMIN)

## Prerequisites

- Java 21
- Maven 4.x
- Gmail account with App Password enabled

## Setup

### 1. Clone the repository

```bash
git clone 
cd 
```

### 2. Configure environment variables

Rename `.env.example` to `.env` in the root directory:

```bash
cp .env.example .env
```

### 3. Set up Gmail for 2FA

1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable 2-Step Verification
3. Generate an App Password
4. Add to `.env`:

```properties
EMAIL_USR=your-email@gmail.com
EMAIL_PWD=your-16-char-app-password
```

### 4. Generate JWT Secret

```bash
openssl rand -hex 32
```

Add the output to `.env`:

```properties
JWT_SECRET=your-generated-secret
```

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using IDE

Run the main application class directly from your IDE (IntelliJ IDEA, Eclipse, VS Code)

The API will be available at: `http://localhost:8080`

## Database Configuration

The application uses H2 in-memory database. Configuration is in [application.yaml](src/main/resources/application.yaml).

**Development mode** (clears data on shutdown):

```yaml
jpa:
  hibernate:
    ddl-auto: create-drop
```

**Persistence mode** (keeps data between restarts):

```yaml
jpa:
  hibernate:
    ddl-auto: update
```

### Accessing H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: Check `application.yaml` for configured path

## Testing the API

### Using IntelliJ HTTP Client

Use [requests.http](requests.http) file:

1. Replace placeholder emails with your real email address
2. Execute requests in order (signup → login → verify → protected endpoints)
3. Use verification codes received via email

### Using Postman

Import the collection: [postman_collection.json](/docs/postman_collection.json)

### Example Flow

1. **Signup**: `POST /api/v1/auth/signup`

```json
{
  "name": "John Doe",
  "email": "your-email@gmail.com",
  "password": "SecurePass123",
  "role": "USER"
}
```

2. **Login**: `POST /api/v1/auth/login`

```json
{
  "email": "your-email@gmail.com",
  "password": "SecurePass123"
}
```

3. **Check your email** for 2FA code

4. **Verify 2FA**: `POST /api/v1/auth/verify`

```json
{
  "email": "your-email@gmail.com",
  "code": "123456"
}
```

5. **Use JWT token** in Authorization header for protected endpoints:

```
Authorization: Bearer <your-jwt-token>
```

## Project Structure

```
├── data/           # db files
├── docs/           # Postman collection
├── src/main/java/ee/lio/
├── config/          # Bean configurations
├── controller/      # REST endpoints
├── converter/       # Mapper
├── dto/             # Request/Response DTOs
├── exception/       # Custom exceptions
├── model/           # Entities
├── repository/      # Data access layer
├── security/        # JWT filters, authentication
├── service/         # Business logic
└── utils/           # JWT Util

```

## Key Design Decisions

- **No Lombok**: Explicit code for learning purposes
- **Separation of Concerns**: Clear layering (Controller/Service/Repository)
- **DTO Pattern**: Request/Response objects separate from entities
- **PUT vs PATCH**: Separate DTOs for full vs partial updates
- **Authorization**: Users can modify own data, ADMIN can modify all
- **Exception Handling**: Global exception handler with proper HTTP status codes

## Learning Focus

This project demonstrates:

- RESTful API design principles
- Spring Security with JWT
- Role-based access control (RBAC)
- Email integration for 2FA
- Proper HTTP status codes (200, 201, 204, 400, 401, 403, 404)
- Request validation
- Clean code architecture
- Exception handling strategies

## Future Enhancements

- [ ] Add refresh token mechanism
- [ ] Implement password reset functionality
- [ ] Add pagination for user listing
- [ ] Docker containerization
- [ ] Integration tests
- [ ] API documentation with Swagger/OpenAPI

## License

MIT License - feel free to use for learning purposes

## Contact

[Aleksei Gedz] - [https://www.linkedin.com/in/aleksei-gedz-b1ba5561/]