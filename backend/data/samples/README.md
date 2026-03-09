# Sample Data Files

This directory contains sample CSV files for testing the Sales & Store Analytics system.

## Files

1. **sample_regions.csv** - 5 regions
2. **sample_products.csv** - 50 products across 5 brands and multiple categories
3. **sample_stores.csv** - 20 stores distributed across 5 regions
4. **sample_sales.csv** - 100 sales records spanning 2024 and 2023 (for YoY comparison)

## Data Loading Order

**IMPORTANT:** Load files in this exact order due to foreign key dependencies:

1. Load `sample_regions.csv` first
2. Load `sample_products.csv` second
3. Load `sample_stores.csv` third (depends on regions)
4. Load `sample_sales.csv` last (depends on products and stores)

## How to Load Data

### Option 1: Using Swagger UI (Recommended)

1. Start the backend application:

   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. Open Swagger UI: http://localhost:8080/swagger-ui.html

3. Navigate to "Data Ingestion" section

4. Use the `POST /api/data/upload/csv` endpoint for each file:
   - Click "Try it out"
   - Click "Choose File" and select the CSV file
   - Click "Execute"
   - Verify the response shows successful ingestion

5. Load files in the order specified above

### Option 2: Using cURL

```bash
# Load regions
curl -X POST "http://localhost:8080/api/data/upload/csv" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@data/samples/sample_regions.csv"

# Load products
curl -X POST "http://localhost:8080/api/data/upload/csv" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@data/samples/sample_products.csv"

# Load stores
curl -X POST "http://localhost:8080/api/data/upload/csv" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@data/samples/sample_stores.csv"

# Load sales
curl -X POST "http://localhost:8080/api/data/upload/csv" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@data/samples/sample_sales.csv"
```

### Option 3: Using Postman

1. Create a new POST request to `http://localhost:8080/api/data/upload/csv`
2. Go to "Body" tab
3. Select "form-data"
4. Add key "file" with type "File"
5. Choose the CSV file
6. Send the request
7. Repeat for each file in order

## Verifying Data

After loading all files, verify the data using H2 Console:

1. Open H2 Console: http://localhost:8080/h2-console
2. Use connection details:
   - JDBC URL: `jdbc:h2:file:./data/analytics`
   - Username: `sa`
   - Password: (leave empty)
3. Run queries:
   ```sql
   SELECT COUNT(*) FROM regions;    -- Should return 5
   SELECT COUNT(*) FROM products;   -- Should return 50
   SELECT COUNT(*) FROM stores;     -- Should return 20
   SELECT COUNT(*) FROM sales;      -- Should return 100
   ```

## Testing APIs

After loading data, test the analytics APIs:

### Sales APIs

- `GET /api/sales/by-brand?startDate=2024-01-01&endDate=2024-03-31`
- `GET /api/sales/total?startDate=2024-01-01&endDate=2024-03-31`
- `GET /api/sales/yoy-comparison?startDate=2024-01-01&endDate=2024-02-29`

### Store APIs

- `GET /api/stores/active-count?startDate=2024-01-01&endDate=2024-03-31`
- `GET /api/stores/active-by-region?startDate=2024-01-01&endDate=2024-03-31`

## Sample Data Characteristics

- **Brands**: Brand A, Brand B, Brand C, Brand D, Brand E
- **Categories**: Snacks, Food, Beverages, Condiments, Confectionery, Dairy
- **Regions**: North, South, East, West, Central
- **Date Range**: January 2023 - March 2024 (for YoY testing)
- **Sales Values**: Range from $150 to $750 per transaction

## Troubleshooting

**Error: "Region not found"**

- Make sure you loaded regions.csv before stores.csv

**Error: "Product not found" or "Store not found"**

- Make sure you loaded products.csv and stores.csv before sales.csv

**Error: "Duplicate key"**

- The database already contains data. Either:
  - Delete the `./data/analytics.mv.db` file and restart
  - Or use different codes in your CSV files
