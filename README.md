# Sales & Store Analytics System

Full-stack FMCG sales and store analytics dashboard with Spring Boot backend and React frontend.

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+** (or use Maven Wrapper)
- **Node.js 18+** and npm
- **Git**

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd Growz_fullstack_assignment
```

### 2. Backend Setup

```bash
cd backend

# Build the project
mvn clean install

# Start the backend (imports 22,762 rows automatically)
mvn spring-boot:run
```

**Wait for**: `Successfully imported 22762 sales transactions` message (takes 2-3 minutes)

Backend runs on: http://localhost:8080

### 3. Frontend Setup

```bash
cd ../frontend

# Install dependencies
npm install

# Start the development server
npm run dev
```

Frontend runs on: http://localhost:3001

## 🔄 Database Management

### Clear Database and Restart Backend

**Important**: Always clear the database before restarting to prevent data duplication.

#### Windows PowerShell:

```powershell
# Stop the backend (Ctrl+C if running)

# Clear database files
Remove-Item -Force backend/data/analytics.mv.db, backend/data/analytics.trace.db -ErrorAction SilentlyContinue

# Restart backend
cd backend
mvn spring-boot:run
```

#### Linux/Mac:

```bash
# Stop the backend (Ctrl+C if running)

# Clear database files
rm -f backend/data/analytics.mv.db backend/data/analytics.trace.db

# Restart backend
cd backend
mvn spring-boot:run
```

## 📊 Features

### Sales Analytics

- Total sales with YoY comparison
- Sales by brand, category, region
- Monthly sales trends
- Top products analysis

### Store Analytics

- Active store count (stores with net sales > 0)
- Active stores by brand and region
- Monthly active store trends
- YoY active store comparison

### Data Import

- Automatic Excel import on startup (22,762 transactions)
- All 32 columns imported correctly
- Support for CSV and Excel uploads via API

## 🔗 Access Points

### Backend

- **API Base**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/analytics`
  - Username: `sa`
  - Password: (leave empty)

### Frontend

- **Dashboard**: http://localhost:3001

## 📁 Project Structure

```
Growz_fullstack_assignment/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/java/com/growz/analytics/
│   │   │   ├── controller/  # REST API endpoints
│   │   │   ├── service/     # Business logic
│   │   │   ├── repository/  # Data access layer
│   │   │   ├── entity/      # JPA entities
│   │   │   ├── dto/         # Data Transfer Objects
│   │   │   └── config/      # Configuration
│   │   └── test/            # Unit and integration tests
│   ├── data/                # H2 database files
│   ├── sales_data.xlsx      # Source data file
│   └── pom.xml
│
└── frontend/                # React frontend
    ├── src/
    │   ├── components/      # React components
    │   ├── services/        # API services
    │   ├── types/           # TypeScript types
    │   └── App.tsx
    └── package.json
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

## 📝 API Documentation

### Sales Endpoints

- `GET /api/sales/total?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/sales/by-brand?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/sales/by-category?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/sales/by-region?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/sales/by-month?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/sales/top-products?startDate=2024-01-01&endDate=2024-12-31&page=0&size=10`
- `GET /api/sales/yoy-comparison?startDate=2024-01-01&endDate=2024-12-31`

### Store Endpoints

- `GET /api/stores/active-count?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/stores/active-by-brand?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/stores/active-by-region?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/stores/active-by-month?startDate=2024-01-01&endDate=2024-12-31`
- `GET /api/stores/yoy-comparison?startDate=2024-01-01&endDate=2024-12-31`

## 🐛 Troubleshooting

### Backend won't start

- Ensure Java 17+ is installed: `java -version`
- Clear database files and restart (see Database Management section)
- Check port 8080 is not in use

### Frontend won't start

- Ensure Node.js 18+ is installed: `node -v`
- Delete `node_modules` and reinstall: `rm -rf node_modules && npm install`
- Check port 3001 is not in use

### Data not showing correctly

- Clear browser cache
- Clear database and restart backend
- Verify data import completed: Check backend logs for "Successfully imported 22762"

### Active stores count incorrect

- The system counts stores by `customer_account_name` (actual store names)
- Active store = store with net sales > 0 for the selected period
- KPI card shows latest month's value, not sum of all months

## 🔧 Configuration

### Backend (`backend/src/main/resources/application.properties`)

```properties
# Database
spring.datasource.url=jdbc:h2:file:./data/analytics
spring.datasource.username=sa
spring.datasource.password=

# Server
server.port=8080

# CORS
cors.allowed-origins=http://localhost:3001
```

### Frontend (`frontend/vite.config.ts`)

```typescript
server: {
  port: 3001,
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

## 📄 License

This project is part of the Growz Full Stack Developer Assessment.

## 👥 Author

Developed as part of the Growz technical assessment.
