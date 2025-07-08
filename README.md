# Is This Person Real? 🤖👤

A comprehensive AI-powered web application that helps verify if social media profiles are authentic or fake. Uses machine learning to analyze profile pictures, web scraping to gather data across platforms, and advanced algorithms to score credibility.

![Project Banner](https://img.shields.io/badge/AI-Face%20Detection-blue?style=for-the-badge&logo=robot)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6.3.0-green?style=for-the-badge&logo=spring)
![React](https://img.shields.io/badge/React-18.3.1-blue?style=for-the-badge&logo=react)
![Python](https://img.shields.io/badge/Python-3.9+-yellow?style=for-the-badge&logo=python)

## 🎯 Project Overview

This project implements a sophisticated AI face detection system with a microservices architecture:

- **Frontend**: Modern React/TypeScript application with real-time image analysis
- **Backend**: Spring Boot GraphQL API with JWT authentication
- **ML Service**: Python FastAPI microservice for AI model serving
- **Database**: MySQL with Redis caching for high performance
- **AI Integration**: OpenAI API for enhanced analysis

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Spring Boot   │    │   Python ML     │
│   (React/TS)    │◄──►│   GraphQL API   │◄──►│   Service       │
│                 │    │                 │    │   (FastAPI)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   MySQL DB      │
                       │   + Redis Cache │
                       └─────────────────┘
```

## ✨ Features

### 🔍 AI Face Detection
- **Deep Learning Analysis**: Uses advanced ML models to detect AI-generated faces
- **Multiple Detection Methods**: Face recognition, image quality analysis, metadata examination
- **Confidence Scoring**: Provides detailed confidence scores for each analysis
- **Real-time Processing**: Fast analysis with results in under 10 seconds

### 🔐 Security & Authentication
- **JWT Authentication**: Secure token-based authentication
- **Role-based Access**: User, Admin, and Moderator roles
- **Input Validation**: Comprehensive validation and sanitization
- **CORS Protection**: Configurable cross-origin request handling

### 📊 Analytics & Monitoring
- **Real-time Statistics**: Live dashboard with analysis metrics
- **Performance Monitoring**: Processing time and queue status tracking
- **User Analytics**: Individual user analysis history and statistics
- **Health Checks**: Comprehensive system health monitoring

### 🚀 High Performance
- **Redis Caching**: Optimized caching for improved response times
- **Asynchronous Processing**: Queue-based processing for high throughput
- **Database Optimization**: Optimized MySQL queries and indexing
- **Load Balancing**: Ready for horizontal scaling

## 🛠️ Technology Stack

### Frontend
- **React 18.3.1** - Modern UI framework
- **TypeScript** - Type-safe development
- **Tailwind CSS** - Utility-first styling
- **Vite** - Fast build tool
- **Lucide React** - Beautiful icons

### Backend
- **Spring Boot 3.2.0** - Java framework
- **GraphQL** - Flexible API querying
- **JWT** - Secure authentication
- **MySQL 8.0** - Primary database
- **Redis** - Caching layer
- **JUnit** - Testing framework

### ML Service
- **FastAPI** - Python web framework
- **OpenCV** - Computer vision
- **TensorFlow** - Machine learning
- **DeepFace** - Face recognition
- **Transformers** - AI models

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-service orchestration
- **Maven** - Java build tool
- **Postman** - API testing

## 📋 Prerequisites

Before running this project, ensure you have the following installed:

- **Java 17+** - For Spring Boot backend
- **Node.js 16+** - For React frontend
- **Python 3.9+** - For ML service
- **MySQL 8.0+** - Database
- **Redis 6.0+** - Caching
- **Docker** (optional) - For containerized deployment

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd isthispersonreal
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start all services**
   ```bash
   docker-compose up -d
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - GraphQL Playground: http://localhost:8080/api/graphiql
   - ML Service: http://localhost:8000

### Option 2: Manual Setup

#### 1. Database Setup
```sql
CREATE DATABASE isthispersonreal;
CREATE USER 'isthispersonreal'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON isthispersonreal.* TO 'isthispersonreal'@'localhost';
FLUSH PRIVILEGES;
```

#### 2. Backend Setup
```bash
cd backend

# Set environment variables
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your-secure-jwt-secret
export OPENAI_API_KEY=your-openai-key

# Build and run
mvn clean install
mvn spring-boot:run
```

#### 3. ML Service Setup
```bash
cd backend/ml-service

# Install dependencies
pip install -r requirements.txt

# Run the service
python main.py
```

#### 4. Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

## 📚 API Documentation

### GraphQL Endpoints
- **Playground**: `http://localhost:8080/api/graphiql`
- **Endpoint**: `http://localhost:8080/api/graphql`

### REST Endpoints
- **Authentication**: `/api/auth/*`
- **Analysis**: `/api/analysis/*`
- **Health**: `/api/actuator/health`

### Example Queries

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

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

### API Testing
Import the provided Postman collection:
```bash
# Import postman_collection.json into Postman
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `password` |
| `JWT_SECRET` | JWT signing secret | (required) |
| `OPENAI_API_KEY` | OpenAI API key | (optional) |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PASSWORD` | Redis password | (optional) |

### Application Properties

Key configuration files:
- `backend/src/main/resources/application.yml` - Backend configuration
- `backend/ml-service/config.py` - ML service configuration
- `frontend/vite.config.ts` - Frontend configuration

## 📊 Monitoring & Health Checks

### Health Endpoints
- **Application Health**: `GET /api/actuator/health`
- **Application Info**: `GET /api/actuator/info`
- **Metrics**: `GET /api/actuator/metrics`
- **ML Service Health**: `GET http://localhost:8000/health`

### Logging
- **Backend**: Structured logging with configurable levels
- **Frontend**: Console logging with error tracking
- **ML Service**: Detailed analysis logging

## 🚀 Deployment

### Production Deployment

1. **Set production environment variables**
2. **Configure production database**
3. **Set up SSL/TLS certificates**
4. **Configure load balancer**
5. **Set up monitoring and alerting**

### Docker Deployment
```bash
# Build and deploy with Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# Or build individual services
docker build -t ai-face-detection-frontend ./frontend
docker build -t ai-face-detection-backend ./backend
docker build -t ai-face-detection-ml ./backend/ml-service
```

### Cloud Deployment
The application is ready for deployment on:
- **AWS** - ECS, EKS, or EC2
- **Google Cloud** - GKE or Compute Engine
- **Azure** - AKS or App Service
- **Heroku** - Container deployment

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
4. **Add tests for new functionality**
5. **Ensure all tests pass**
   ```bash
   # Backend tests
   cd backend && mvn test
   
   # Frontend tests
   cd frontend && npm test
   ```
6. **Submit a pull request**

### Development Guidelines
- Follow the existing code style
- Add comprehensive tests
- Update documentation
- Ensure security best practices

## 📁 Project Structure

```
isthispersonreal/
├── frontend/                 # React TypeScript application
│   ├── src/
│   │   ├── App.tsx          # Main application component
│   │   ├── main.tsx         # Application entry point
│   │   └── index.css        # Global styles
│   ├── package.json         # Frontend dependencies
│   └── vite.config.ts       # Vite configuration
├── backend/                  # Spring Boot application
│   ├── src/main/java/
│   │   └── com/isthispersonreal/api/
│   │       ├── config/      # Configuration classes
│   │       ├── controller/  # REST controllers
│   │       ├── graphql/     # GraphQL resolvers
│   │       ├── model/       # Entity models
│   │       ├── repository/  # Data access layer
│   │       ├── security/    # Security configuration
│   │       └── service/     # Business logic
│   ├── ml-service/          # Python ML microservice
│   │   ├── main.py          # FastAPI application
│   │   ├── ml_engine.py     # ML model engine
│   │   └── requirements.txt # Python dependencies
│   ├── pom.xml              # Maven configuration
│   └── Dockerfile           # Backend containerization
├── docker-compose.yml       # Complete stack orchestration
├── .env.example             # Environment variables template
└── README.md               # This file
```

## 🐛 Troubleshooting

### Common Issues

#### Backend Won't Start
- Check MySQL connection and credentials
- Verify Redis is running
- Ensure all environment variables are set

#### ML Service Issues
- Check Python dependencies are installed
- Verify model files are accessible
- Check GPU drivers (if using GPU acceleration)

#### Frontend Build Issues
- Clear node_modules and reinstall: `rm -rf node_modules && npm install`
- Check Node.js version compatibility
- Verify all environment variables are set

#### Database Connection Issues
- Verify MySQL service is running
- Check database credentials
- Ensure database exists and user has permissions

### Getting Help

1. **Check the logs**:
   ```bash
   # Backend logs
   docker logs ai-face-detection-api
   
   # ML service logs
   docker logs ai-face-detection-ml
   
   # Frontend logs
   docker logs ai-face-detection-frontend
   ```

2. **Check health endpoints**:
   ```bash
   curl http://localhost:8080/api/actuator/health
   curl http://localhost:8000/health
   ```

3. **Create an issue** with detailed information about your problem

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **OpenAI** for AI analysis capabilities
- **Spring Boot** team for the excellent framework
- **React** team for the amazing frontend library
- **FastAPI** team for the Python web framework
- **OpenCV** and **TensorFlow** for computer vision capabilities

## 📞 Support

For support and questions:
- 📧 **Email**: support@isthispersonreal.com
- 🐛 **Issues**: [GitHub Issues](https://github.com/your-repo/issues)
- 📖 **Documentation**: [Project Wiki](https://github.com/your-repo/wiki)
- 💬 **Discord**: [Join our community](https://discord.gg/your-server)

---

**Built with ❤️ by [@jvalaj](https://github.com/jvalaj)**

*This project helps combat online deception by providing reliable AI-generated face detection tools.* 