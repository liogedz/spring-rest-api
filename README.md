# JAVA SPRINGBOOT / ANGULAR CRUD REST API

A comprehensive REST API demonstrating Spring Boot best practices, authentication, and CRUD operations.

## Features

- 🔐 JWT-based authentication with 2FA (email verification)
- 🔑 Oauth login with `Google` and `GitHub`, saves user into DB
- 📧 Email service integration (Gmail)
- 👤 User management with role-based access control (USER/ADMIN)
- ✅ Full CRUD operations (GET, POST, PUT, PATCH, DELETE)
- 🛡️ Global exception handling
- 📝 Bean validation
- 🗄️ H2 in-memory database
- 🎯 Clean architecture (Controller → Service → Repository)
- 🅰️ Angular full Signals Forms and Signals based architecture

## Tech Stack

- Java 21 or later
- Spring Boot 4.x
- Spring Security
- Spring Data JPA
- H2 Database
- JWT (JSON Web Tokens)
- Maven
- JavaMail API
- Angular 21.1.1

## API Endpoints

### Authentication

- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login (triggers 2FA email)
- `POST /api/auth/verify` - Verify code and receive JWT token
- `POST /api/auth/set-password` - Set password for Oauth logged users
- `POST /api/auth/forgot-password` - Initializes forgot password logic
- `GET /api/auth/validate-reset-password` - Compares token created for forgot-password and one received via e-mail
- `POST /api/auth/reset-password` - Sets new password

### Users (Protected)

- `GET /api/users` - Get all users (ADMIN only)
- `GET /api/user/{id}` - Get user by ID
- `GET /api/user/current-user` - Get current user
- `PUT /api/user/{id}` - Full update (own account or ADMIN)
- `PATCH /api/user/{id}` - Partial update (own account or ADMIN)
- `DELETE /api/user/{id}` - Delete user (own account or ADMIN)

## Prerequisites

- Java 21
- Maven 4.x
- Angular 21.1.2
- Gmail account with App Password enabled

## Setup

### 1. Clone the repository

```bash
git clone 
cd 
```

### 2. Configure environment variables

Rename `.env.example` to `.env` in the [resources](/backend/src/main/resources) directory:

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
cd backend
mvn spring-boot:run
```

```bash
cd frontend
npm install
ng serve
```

### Using IDE

Run the main application class directly from your IDE (IntelliJ IDEA, Eclipse, VS Code)

The API will be available at: `http://localhost:8080`

## Database Configuration

The application uses H2 in-memory database. Configuration is
in [application.yaml](backend/src/main/resources/application.yaml).

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

Use [requests.http](backend/requests.http) file:

1. Replace placeholder emails with your real email address
2. Execute requests in order (signup → login → verify → protected endpoints)
3. Use verification codes received via email

### Using Postman

Import the collection: [postman_collection.json](/backend/docs/postman_collection.json)

### Example Flow

1. **Signup**: `POST /api/auth/signup`

```json
{
  "name": "John Doe",
  "email": "your-email@gmail.com",
  "password": "SecurePass123",
  "role": "USER"
}
```

2. **Login**: `POST /api/auth/login`

```json
{
  "email": "your-email@gmail.com",
  "password": "SecurePass123"
}
```

3. **Check your email** for 2FA code

4. **Verify 2FA**: `POST /api/auth/verify`

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

## Key Design Decisions

- **No Lombok**: Explicit code for learning purposes
- **Separation of Concerns**: Clear layering (Controller/Service/Repository)
- **DTO Pattern**: Request/Response objects separate from entities
- **PUT vs PATCH**: Separate DTOs for full vs partial updates
- **Authorization**: Users can modify own data (besides the role), ADMIN can modify all. OAuth users are always created
  with role USER.
- **OAuth**: Enforces everyone to set password on first login
- **Exception Handling**: Global exception handler with proper HTTP status codes

## Frontend Notes

- Angular uses Signals-based state management
- HTTP interceptors attach JWT automatically
- Auth state is derived from token presence

## Token Policies

- JWT access token: 10 hours
- 2FA verification code: 5 minutes
- Password reset token: single-use, 15 minutes

## Security Notes

- H2 is used for development only
- JWT secret is loaded from environment variables
- HTTPS is required in production
- OAuth client secrets must never be committed

## CORS Configuration

CORS is configured to allow requests from:

- http://localhost:4200 (Angular dev server)

Adjust allowed origins in `application.yaml` for production.

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
- [x] Implement password reset functionality
- [x] Add pagination for user listing
- [ ] Integration tests

## License

MIT License - feel free to use for learning purposes

## Contact

[Aleksei Gedz] - [https://www.linkedin.com/in/aleksei-gedz-b1ba5561/]
