import apiClient from './apiService';
import { Dayjs } from 'dayjs';

export interface MetricDTO {
  name: string;
  value: number;
}

export interface TimeSeriesDTO {
  year: number;
  month: number;
  value: number;
}

export interface YoYComparisonDTO {
  currentValue: number;
  previousValue: number;
  percentageChange: number | null;
}

export interface IngestionResultDTO {
  successCount: number;
  errorCount: number;
  errorMessages: string[];
}

const formatDate = (date: Dayjs): string => {
  return date.format('YYYY-MM-DD');
};

export const analyticsService = {
  // Sales endpoints
  async getTotalSales(startDate: Dayjs, endDate: Dayjs): Promise<number> {
    const response = await apiClient.get('/api/sales/total', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getSalesByBrand(startDate: Dayjs, endDate: Dayjs): Promise<MetricDTO[]> {
    const response = await apiClient.get('/api/sales/by-brand', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getSalesByRegion(startDate: Dayjs, endDate: Dayjs): Promise<MetricDTO[]> {
    const response = await apiClient.get('/api/sales/by-region', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getSalesByCategory(startDate: Dayjs, endDate: Dayjs): Promise<MetricDTO[]> {
    const response = await apiClient.get('/api/sales/by-category', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getSalesByMonth(startDate: Dayjs, endDate: Dayjs): Promise<TimeSeriesDTO[]> {
    const response = await apiClient.get('/api/sales/by-month', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getTopProducts(startDate: Dayjs, endDate: Dayjs, limit: number = 10): Promise<MetricDTO[]> {
    const response = await apiClient.get('/api/sales/top-products', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
        limit,
      },
    });
    return response.data.content || response.data;
  },

  async getSalesYoYComparison(startDate: Dayjs, endDate: Dayjs): Promise<YoYComparisonDTO> {
    const response = await apiClient.get('/api/sales/yoy-comparison', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  // Store endpoints
  async getActiveStoreCount(startDate: Dayjs, endDate: Dayjs): Promise<number> {
    const response = await apiClient.get('/api/stores/active-count', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getActiveStoresByBrand(startDate: Dayjs, endDate: Dayjs): Promise<MetricDTO[]> {
    const response = await apiClient.get('/api/stores/active-by-brand', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getActiveStoresByRegion(startDate: Dayjs, endDate: Dayjs): Promise<MetricDTO[]> {
    const response = await apiClient.get('/api/stores/active-by-region', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getActiveStoresByMonth(startDate: Dayjs, endDate: Dayjs): Promise<TimeSeriesDTO[]> {
    const response = await apiClient.get('/api/stores/active-by-month', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  async getStoresYoYComparison(startDate: Dayjs, endDate: Dayjs): Promise<YoYComparisonDTO> {
    const response = await apiClient.get('/api/stores/yoy-comparison', {
      params: {
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
      },
    });
    return response.data;
  },

  // Fetch all sales metrics in parallel
  async getSalesMetrics(startDate: Dayjs, endDate: Dayjs) {
    const [
      totalSales,
      yoyComparison,
      salesByBrand,
      salesByRegion,
      salesByCategory,
      salesByMonth,
      topProducts,
    ] = await Promise.all([
      this.getTotalSales(startDate, endDate),
      this.getSalesYoYComparison(startDate, endDate),
      this.getSalesByBrand(startDate, endDate),
      this.getSalesByRegion(startDate, endDate),
      this.getSalesByCategory(startDate, endDate),
      this.getSalesByMonth(startDate, endDate),
      this.getTopProducts(startDate, endDate, 10),
    ]);

    return {
      totalSales,
      yoyComparison,
      salesByBrand,
      salesByRegion,
      salesByCategory,
      salesByMonth,
      topProducts,
    };
  },

  // Fetch all store metrics in parallel
  async getStoreMetrics(startDate: Dayjs, endDate: Dayjs) {
    const [
      activeStoreCount,
      yoyComparison,
      activeStoresByBrand,
      activeStoresByRegion,
      activeStoresByMonth,
    ] = await Promise.all([
      this.getActiveStoreCount(startDate, endDate),
      this.getStoresYoYComparison(startDate, endDate),
      this.getActiveStoresByBrand(startDate, endDate),
      this.getActiveStoresByRegion(startDate, endDate),
      this.getActiveStoresByMonth(startDate, endDate),
    ]);

    return {
      activeStoreCount,
      yoyComparison,
      activeStoresByBrand,
      activeStoresByRegion,
      activeStoresByMonth,
    };
  },

  // Data ingestion endpoints
  async uploadCSV(file: File): Promise<IngestionResultDTO> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post('/api/data/upload/csv', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  async uploadExcel(file: File): Promise<IngestionResultDTO> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post('/api/data/upload/excel', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },
};
