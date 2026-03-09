import React, { useState, useEffect } from 'react';
import { Loader2, AlertCircle } from 'lucide-react';
import { useFilters } from '../contexts/FilterContext';
import { analyticsService } from '../services/analyticsService';
import KPICard from '../components/KPICard';
import FilterPanel from '../components/FilterPanel';
import ChartContainer from '../components/ChartContainer';
import LineChartComponent from '../components/LineChartComponent';
import BarChartComponent from '../components/BarChartComponent';

const StoresDashboard: React.FC = () => {
  const { filters } = useFilters();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [metrics, setMetrics] = useState<any>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await analyticsService.getStoreMetrics(filters.startDate, filters.endDate);
        setMetrics(data);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch store data');
        console.error('Error fetching store metrics:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [filters.startDate, filters.endDate]);

  if (loading && !metrics) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-center items-center min-h-[60vh]">
          <Loader2 className="w-12 h-12 text-primary animate-spin" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
          <div className="text-red-800">{error}</div>
        </div>
      </div>
    );
  }

  if (!metrics) return null;

  // Format time series data for chart
  const trendData = metrics.activeStoresByMonth.map((item: any) => ({
    period: `${item.year}-${String(item.month).padStart(2, '0')}`,
    value: item.value,
  }));

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">
        Active Stores Dashboard
      </h1>

      <FilterPanel />

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
        <KPICard
          title="Active Stores"
          value={metrics.activeStoreCount}
          yoyChange={metrics.yoyComparison.percentageChange}
        />
        <KPICard
          title="YoY Change"
          value={metrics.yoyComparison.percentageChange || 0}
          suffix="%"
        />
        <KPICard
          title="Previous Period"
          value={metrics.yoyComparison.previousValue}
        />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 gap-6">
        <div className="col-span-1">
          <ChartContainer title="Active Stores Trend" loading={loading}>
            <LineChartComponent
              data={trendData}
              xKey="period"
              yKey="value"
              xLabel="Month"
              yLabel="Active Stores"
              color="#1976d2"
            />
          </ChartContainer>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <ChartContainer title="Active Stores by Region" loading={loading}>
            <BarChartComponent
              data={metrics.activeStoresByRegion}
              xKey="name"
              yKey="value"
              xLabel="Region"
              yLabel="Active Stores"
              color="#4caf50"
              highlightMinMax={true}
            />
          </ChartContainer>

          <ChartContainer title="Active Stores by Brand" loading={loading}>
            <BarChartComponent
              data={metrics.activeStoresByBrand}
              xKey="name"
              yKey="value"
              xLabel="Brand"
              yLabel="Active Stores"
              color="#2196f3"
              highlightMinMax={true}
            />
          </ChartContainer>
        </div>
      </div>
    </div>
  );
};

export default StoresDashboard;
