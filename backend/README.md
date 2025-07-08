# AI Face Detection API Backend

A secure GraphQL API built with Spring Boot for AI-generated face detection, featuring a microservices architecture with Python ML service integration.

## TODO
- [ ] Add rate limiting to API endpoints
- [ ] Implement user email verification
- [ ] Add admin dashboard for monitoring
- [ ] Improve error handling for ML service failures
- [ ] Add more unit and integration tests

## 🚀 Features

- **GraphQL API** with comprehensive schema for face detection analysis
- **JWT Authentication** with role-based access control
- **Microservices Architecture** with Python ML service
- **Redis Caching** for improved performance
- **MySQL Database** with optimized resolvers
- **OpenAI Integration** for enhanced analysis
- **Asynchronous Processing** with queue management
- **Comprehensive Testing** with JUnit and Postman support

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Spring Boot   │    │   Python ML     │
│   (React)       │◄──►│   GraphQL API   │◄──►│   Service       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   MySQL DB      │
                       │   + Redis Cache │
                       └─────────────────┘
```

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Python 3.8+ (for ML service)
- Node.js 16+ (for development)

## 🛠️ Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd backend
```

### 2. Database Setup
```sql
CREATE DATABASE isthispersonreal;
CREATE USER 'isthispersonreal'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON isthispersonreal.* TO 'isthispersonreal'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Environment Configuration
Create `application-local.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/isthispersonreal
    username: your_username
    password: your_password

jwt:
  secret: your-secure-jwt-secret-key

openai:
  api-key: your-openai-api-key

ml-service:
  url: http://localhost:8000
```

### 4. Build and Run Spring Boot Application
```bash
mvn clean install
mvn spring-boot:run
```

### 5. Setup Python ML Service
```bash
cd ml-service
pip install -r requirements.txt
python main.py
```

## 🔧 Configuration

### Application Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8080 |
| `spring.datasource.url` | Database URL | jdbc:mysql://localhost:3306/isthispersonreal |
| `jwt.secret` | JWT signing secret | (required) |
| `jwt.expiration` | JWT expiration time | 86400000 (24h) |
| `openai.api-key` | OpenAI API key | (optional) |
| `ml-service.url` | Python ML service URL | http://localhost:8000 |

### Environment Variables

```bash
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret
export OPENAI_API_KEY=your_openai_key
export REDIS_HOST=localhost
export REDIS_PASSWORD=your_redis_password
```

## 📚 API Documentation

### GraphQL Endpoints

- **GraphQL Playground**: `http://localhost:8080/api/graphiql`
- **GraphQL Endpoint**: `http://localhost:8080/api/graphql`

### REST Endpoints

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/validate` - Token validation
- `GET /api/auth/profile` - User profile

#### Analysis
- `POST /api/analysis/upload` - Upload image for analysis
- `GET /api/analysis/result/{imageHash}` - Get analysis result
- `GET /api/analysis/stats` - Get analysis statistics
- `POST /api/analysis/social-media` - Social media analysis

### Example GraphQL Queries

#### Upload and Analyze Image
```graphql
mutation UploadImage($file: Upload!) {
  uploadImage(file: $file) {
    id
    label
    confidence
    scores {
      type
      value
    }
    processingTimeMs
    createdAt
  }
}
```

#### Get Analysis Statistics
```graphql
query GetStats {
  analysisStats {
    totalAnalyses
    realCount
    aiGeneratedCount
    uncertainCount
    pendingJobs
    processingJobs
    completedJobs
    failedJobs
  }
}
```

#### User Profile
```graphql
query GetProfile {
  userProfile {
    id
    username
    email
    role
    analysisCount
    averageConfidence
    createdAt
    lastLogin
  }
}
```

## 🔐 Security

### JWT Authentication
- Token-based authentication with configurable expiration
- Role-based access control (USER, ADMIN, MODERATOR)
- Secure password hashing with BCrypt

### CORS Configuration
- Configurable CORS settings for cross-origin requests
- Support for multiple origins

### Input Validation
- Comprehensive input validation and sanitization
- File upload restrictions and validation

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Postman Collection
Import the provided Postman collection for API testing:
- Authentication flows
- Image upload and analysis
- GraphQL queries
- Error handling

## 📊 Monitoring

### Health Checks
- `GET /api/actuator/health` - Application health
- `GET /api/actuator/info` - Application information
- `GET /api/actuator/metrics` - Application metrics

### Logging
- Structured logging with configurable levels
- Request/response logging
- Error tracking and monitoring

## 🚀 Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t ai-face-detection-api .

# Run with Docker Compose
docker-compose up -d
```

### Production Configuration
1. Set production environment variables
2. Configure production database
3. Set up SSL/TLS certificates
4. Configure load balancer
5. Set up monitoring and alerting

## 🔧 Development

### Code Structure
```
src/main/java/com/isthispersonreal/api/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── graphql/         # GraphQL resolvers
├── model/           # Entity models
├── repository/      # Data access layer
├── security/        # Security configuration
├── service/         # Business logic
└── queue/           # Async processing

ml-service/
├── main.py          # FastAPI application
├── ml_engine.py     # ML model engine
├── config.py        # Configuration
└── requirements.txt # Python dependencies
```

### Adding New Features
1. Create entity model in `model/` package
2. Add repository interface in `repository/` package
3. Implement business logic in `service/` package
4. Add GraphQL schema in `schema.graphqls`
5. Create resolver in `graphql/` package
6. Add REST endpoints in `controller/` package
7. Write tests for all components

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 🔄 Version History

- **v1.0.0** - Initial release with core functionality
- **v1.1.0** - Added social media analysis
- **v1.2.0** - Enhanced ML model and performance improvements 