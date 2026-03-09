import React, { useState, useEffect } from 'react';
import { Loader2, AlertCircle } from 'lucide-react';
import { useFilters } from '../contexts/FilterContext';
import { analyticsService } from '../services/analyticsService';
import KPICard from '../components/KPICard';
import FilterPanel from '../components/FilterPanel';
import ChartContainer from '../components/ChartContainer';
import LineChartComponent from '../components/LineChartComponent';
import BarChartComponent from '../components/BarChartComponent';

const SalesDashboard: React.FC = () => {
  const { filters } = useFilters();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [metrics, setMetrics] = useState<any>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await analyticsService.getSalesMetrics(filters.startDate, filters.endDate);
        setMetrics(data);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch sales data');
        console.error('Error fetching sales metrics:', err);
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

  const topBrand = metrics.salesByBrand.length > 0
    ? metrics.salesByBrand.reduce((prev: any, current: any) =>
        prev.value > current.value ? prev : current
      )
    : null;

  // Format time series data for chart
  const trendData = metrics.salesByMonth.map((item: any) => ({
    period: `${item.year}-${String(item.month).padStart(2, '0')}`,
    value: item.value,
  }));

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">
        Sales Dashboard
      </h1>

      <FilterPanel />

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
        <KPICard
          title="Total Sales"
          value={metrics.totalSales}
          prefix="$"
          yoyChange={metrics.yoyComparison.percentageChange}
        />
        <KPICard
          title="YoY Growth"
          value={metrics.yoyComparison.percentageChange || 0}
          suffix="%"
        />
        <KPICard
          title="Top Brand"
          value={topBrand ? `${topBrand.name} (${topBrand.value.toFixed(2)})` : 'N/A'}
        />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 gap-6">
        <div className="col-span-1">
          <ChartContainer title="Sales Trend" loading={loading}>
            <LineChartComponent
              data={trendData}
              xKey="period"
              yKey="value"
              xLabel="Month"
              yLabel="Sales ($)"
              color="#1976d2"
            />
          </ChartContainer>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <ChartContainer title="Sales by Brand" loading={loading}>
            <BarChartComponent
              data={metrics.salesByBrand}
              xKey="name"
              yKey="value"
              xLabel="Brand"
              yLabel="Sales ($)"
              color="#2196f3"
              highlightMinMax={true}
            />
          </ChartContainer>

          <ChartContainer title="Sales by Region" loading={loading}>
            <BarChartComponent
              data={metrics.salesByRegion}
              xKey="name"
              yKey="value"
              xLabel="Region"
              yLabel="Sales ($)"
              color="#4caf50"
              highlightMinMax={true}
            />
          </ChartContainer>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <ChartContainer title="Sales by Category" loading={loading}>
            <BarChartComponent
              data={metrics.salesByCategory}
              xKey="name"
              yKey="value"
              xLabel="Category"
              yLabel="Sales ($)"
              color="#ff9800"
              highlightMinMax={true}
            />
          </ChartContainer>

          <ChartContainer title="Top 10 Products" loading={loading}>
            <BarChartComponent
              data={metrics.topProducts}
              xKey="name"
              yKey="value"
              xLabel="Product"
              yLabel="Sales ($)"
              color="#9c27b0"
              highlightMinMax={true}
            />
          </ChartContainer>
        </div>
      </div>
    </div>
  );
};

export default SalesDashboard;
