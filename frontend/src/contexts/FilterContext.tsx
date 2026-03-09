import React, { createContext, useContext, useState, ReactNode } from 'react';
import dayjs, { Dayjs } from 'dayjs';

export interface FilterState {
  startDate: Dayjs;
  endDate: Dayjs;
  brands: string[];
  categories: string[];
  regions: string[];
}

interface FilterContextType {
  filters: FilterState;
  updateFilters: (updates: Partial<FilterState>) => void;
  resetFilters: () => void;
}

const FilterContext = createContext<FilterContextType | undefined>(undefined);

const getDefaultFilters = (): FilterState => ({
  startDate: dayjs().subtract(1, 'year'),
  endDate: dayjs(),
  brands: [],
  categories: [],
  regions: [],
});

export const FilterProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [filters, setFilters] = useState<FilterState>(getDefaultFilters());

  const updateFilters = (updates: Partial<FilterState>) => {
    setFilters((prev) => ({ ...prev, ...updates }));
  };

  const resetFilters = () => {
    setFilters(getDefaultFilters());
  };

  return (
    <FilterContext.Provider value={{ filters, updateFilters, resetFilters }}>
      {children}
    </FilterContext.Provider>
  );
};

export const useFilters = (): FilterContextType => {
  const context = useContext(FilterContext);
  if (!context) {
    throw new Error('useFilters must be used within a FilterProvider');
  }
  return context;
};
