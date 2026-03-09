# Sales & Store Analytics Frontend

React-based dashboard for FMCG sales and store analytics.

## Technology Stack

- **React**: 18.2
- **TypeScript**: 5.3
- **Build Tool**: Vite
- **UI Styling**: Tailwind CSS v3
- **Icons**: Lucide React
- **Charts**: Recharts
- **HTTP Client**: Axios
- **Routing**: React Router v6
- **Date Handling**: dayjs

## Why Tailwind CSS?

We chose Tailwind CSS over Material-UI for this project because:

- **Better Performance**: Smaller bundle size (~10KB vs ~300KB for MUI)
- **Faster Development**: Utility-first approach speeds up UI development
- **No Compatibility Issues**: Pure CSS with no complex JavaScript dependencies
- **Full Customization**: Complete control over design without fighting component APIs
- **Modern Standard**: Industry-leading utility-first CSS framework
- **Perfect for Dashboards**: Ideal for data-heavy analytical interfaces

## Prerequisites

- Node.js 18+ and npm

## Setup Instructions

### 1. Install Dependencies

```bash
cd frontend
npm install
```

### 2. Run Development Server

```bash
npm run dev
```

The application will be available at http://localhost:3000

### 3. Build for Production

```bash
npm run build
```

The build output will be in the `build/` directory.

## Project Structure

```
frontend/
├── src/
│   ├── components/     # Shared React components
│   ├── pages/          # Page components (dashboards)
│   ├── services/       # API services
│   ├── contexts/       # React contexts
│   ├── App.tsx         # Main app component
│   └── main.tsx        # Entry point
├── public/             # Static assets
└── index.html          # HTML template
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm test` - Run tests
- `npm run lint` - Lint code

## Environment Variables

Create a `.env` file in the frontend directory:

```
VITE_API_BASE_URL=http://localhost:8080
VITE_CACHE_ENABLED=true
VITE_DEBOUNCE_DELAY=300
```

## Features

- Sales Overview Dashboard
- Active Stores Dashboard
- Interactive charts with drill-down
- Global filters (date range, brand, category, region)
- Data upload interface
- Responsive design

## API Integration

The frontend communicates with the backend API at `http://localhost:8080/api`.

Vite proxy is configured to forward `/api` requests to the backend during development.

## Author

Growz Analytics Team
