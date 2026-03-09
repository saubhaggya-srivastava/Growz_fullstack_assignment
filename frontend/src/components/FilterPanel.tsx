import React from 'react';
import dayjs from 'dayjs';
import { useFilters } from '../contexts/FilterContext';

const FilterPanel: React.FC = () => {
  const { filters, updateFilters } = useFilters();

  return (
    <div className="card mb-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Filters</h3>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label htmlFor="startDate" className="block text-sm font-medium text-gray-700 mb-1">
            Start Date
          </label>
          <input
            type="date"
            id="startDate"
            value={filters.startDate.format('YYYY-MM-DD')}
            onChange={(e) => updateFilters({ startDate: dayjs(e.target.value) })}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          />
        </div>

        <div>
          <label htmlFor="endDate" className="block text-sm font-medium text-gray-700 mb-1">
            End Date
          </label>
          <input
            type="date"
            id="endDate"
            value={filters.endDate.format('YYYY-MM-DD')}
            onChange={(e) => updateFilters({ endDate: dayjs(e.target.value) })}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          />
        </div>
      </div>
    </div>
  );
};

export default FilterPanel;
