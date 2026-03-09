import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import { FilterProvider } from './contexts/FilterContext';
import SalesDashboard from './pages/SalesDashboard';
import StoresDashboard from './pages/StoresDashboard';
import DataUploadPage from './pages/DataUploadPage';
import { BarChart3, Store, Upload } from 'lucide-react';

function Navigation() {
  const location = useLocation();
  
  const isActive = (path: string) => location.pathname === path;
  
  return (
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            <div className="flex-shrink-0 flex items-center">
              <h1 className="text-xl font-bold text-primary-600">Sales & Store Analytics</h1>
            </div>
            <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
              <Link
                to="/"
                className={`inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium ${
                  isActive('/')
                    ? 'border-primary-500 text-gray-900'
                    : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                }`}
              >
                <BarChart3 className="w-4 h-4 mr-2" />
                Sales Dashboard
              </Link>
              <Link
                to="/stores"
                className={`inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium ${
                  isActive('/stores')
                    ? 'border-primary-500 text-gray-900'
                    : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                }`}
              >
                <Store className="w-4 h-4 mr-2" />
                Stores Dashboard
              </Link>
              <Link
                to="/upload"
                className={`inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium ${
                  isActive('/upload')
                    ? 'border-primary-500 text-gray-900'
                    : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                }`}
              >
                <Upload className="w-4 h-4 mr-2" />
                Data Upload
              </Link>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
}

function App() {
  return (
    <Router>
      <FilterProvider>
        <div className="min-h-screen bg-gray-50">
          <Navigation />
          <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <Routes>
              <Route path="/" element={<SalesDashboard />} />
              <Route path="/stores" element={<StoresDashboard />} />
              <Route path="/upload" element={<DataUploadPage />} />
            </Routes>
          </main>
        </div>
      </FilterProvider>
    </Router>
  );
}

export default App;
