# Sales & Store Analytics Backend

Backend service for FMCG sales and store analytics dashboard.

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Database**: H2 (file-based)
- **Build Tool**: Maven
- **Documentation**: Swagger/OpenAPI

## Dependencies

- Spring Web
- Spring Data JPA
- Spring Cache (in-memory)
- H2 Database
- Springdoc OpenAPI
- Apache POI (Excel parsing)
- OpenCSV (CSV parsing)
- Lombok
- jqwik (property-based testing)

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use Maven Wrapper)

## Setup Instructions

### 1. Enable Lombok in IDE

**IntelliJ IDEA:**

- Go to Settings → Build, Execution, Deployment → Compiler → Annotation Processors
- Enable "Enable annotation processing"

**Eclipse:**

- Install Lombok plugin from https://projectlombok.org/

### 2. Build the Project

```bash
cd backend
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/analytics-backend-1.0.0.jar
```

## Access Points

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/analytics`
  - Username: `sa`
  - Password: (leave empty)

## Project Structure

```
backend/
└── src/main/java/com/growz/analytics
    ├── controller/     # REST API endpoints
    ├── service/        # Business logic and caching
    ├── repository/     # Data access layer
    ├── entity/         # JPA entities
    ├── dto/            # Data Transfer Objects
    ├── config/         # Configuration classes
    └── exception/      # Custom exceptions and handlers
```

## Configuration

Key configuration in `application.properties`:

- **Database**: H2 file-based at `./data/analytics`
- **Cache**: Simple in-memory (ConcurrentMapCacheManager)
- **File Upload**: Max 10MB
- **CORS**: Allows http://localhost:3000

## API Endpoints

### Sales Endpoints

- `GET /api/sales/by-brand` - Sales by brand
- `GET /api/sales/by-product` - Sales by product (paginated)
- `GET /api/sales/by-month` - Sales time series
- `GET /api/sales/by-region` - Sales by region
- `GET /api/sales/by-category` - Sales by category
- `GET /api/sales/top-products` - Top N products
- `GET /api/sales/total` - Total sales
- `GET /api/sales/yoy-comparison` - Year-over-year comparison

### Store Endpoints

- `GET /api/stores/active-count` - Active store count
- `GET /api/stores/active-by-brand` - Active stores by brand
- `GET /api/stores/active-by-region` - Active stores by region
- `GET /api/stores/active-by-month` - Active stores time series
- `GET /api/stores/yoy-comparison` - YoY active stores comparison

### Data Ingestion Endpoints

- `POST /api/data/upload/csv` - Upload CSV file
- `POST /api/data/upload/excel` - Upload Excel file

## Testing

Run all tests:

```bash
mvn test
```

Run specific test:

```bash
mvn test -Dtest=SalesServiceTest
```

## Database Management

The application uses H2 file-based database for data persistence. Database files are stored in `./data/` directory.

### Clear Database and Restart

**Important**: Always clear the database before restarting to prevent data duplication.

#### Windows PowerShell:

```powershell
# Stop the backend (Ctrl+C if running)

# Clear database files
Remove-Item -Force data/analytics.mv.db, data/analytics.trace.db -ErrorAction SilentlyContinue

# Restart backend
mvn spring-boot:run
```

#### Linux/Mac:

```bash
# Stop the backend (Ctrl+C if running)

# Clear database files
rm -f data/analytics.mv.db data/analytics.trace.db

# Restart backend
mvn spring-boot:run
```

### Data Import

On startup, the application automatically imports data from `sales_data.xlsx`:

- **22,762 transaction rows** imported
- **All 32 columns** imported correctly
- Import takes 2-3 minutes
- Watch for: `Successfully imported 22762 sales transactions`

### Active Stores Calculation

The system calculates active stores using the following logic:

**Definition**: Active Store = Store with net sales > 0 for the selected period

**Implementation**:

- Groups transactions by `customer_account_name` (actual store name, e.g., "AL MEERA-THUMAMA")
- Calculates `SUM(value)` per store per month
- Filters stores with `SUM(value) > 0` (excludes returns that cancel out sales)
- Counts distinct stores

**Example**:

```sql
-- Monthly active stores
SELECT year, month, COUNT(*) as active_stores
FROM (
  SELECT year, month, customer_account_name, SUM(value) as total_sales
  FROM sales_transactions
  WHERE invoice_date BETWEEN :startDate AND :endDate
  GROUP BY year, month, customer_account_name
  HAVING SUM(value) > 0
) sub
GROUP BY year, month
ORDER BY year, month;
```

**Expected Values** (2024 data):

- Jan: 152 stores
- Feb: 139 stores
- Mar: 139 stores
- Apr: 145 stores
- May: 132 stores
- Jun: 139 stores
- Jul: 201 stores
- Aug: 201 stores
- Sep: 134 stores
- Oct: 220 stores
- Nov: 508 stores
- Dec: 470 stores

**KPI Card**: Shows latest month's value (e.g., Dec = 470), NOT sum of all months

## Notes

- Caching is enabled using Spring's simple in-memory cache (no TTL support)
- For production, consider migrating to Caffeine or Redis for TTL support
- H2 console is enabled for debugging purposes
- All timestamps are in UTC

## Author

Growz Analytics Team
