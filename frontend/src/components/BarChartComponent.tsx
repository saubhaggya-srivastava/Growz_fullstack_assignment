import React from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  Cell,
} from 'recharts';

interface BarChartComponentProps {
  data: any[];
  xKey: string;
  yKey: string;
  xLabel?: string;
  yLabel?: string;
  color?: string;
  highlightMinMax?: boolean;
}

const BarChartComponent: React.FC<BarChartComponentProps> = ({
  data,
  xKey,
  yKey,
  xLabel = '',
  yLabel = '',
  color = '#8884d8',
  highlightMinMax = false,
}) => {
  const formatTooltipValue = (value: number) => {
    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2,
    }).format(value);
  };

  const getBarColor = (index: number) => {
    if (!highlightMinMax || data.length === 0) return color;

    const values = data.map((item) => item[yKey]);
    const maxValue = Math.max(...values);
    const minValue = Math.min(...values);
    const currentValue = data[index][yKey];

    if (currentValue === maxValue) return '#4caf50'; // Green for max
    if (currentValue === minValue) return '#f44336'; // Red for min
    return color;
  };

  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey={xKey} label={{ value: xLabel, position: 'insideBottom', offset: -5 }} />
        <YAxis label={{ value: yLabel, angle: -90, position: 'insideLeft' }} />
        <Tooltip formatter={formatTooltipValue} />
        <Legend />
        <Bar dataKey={yKey}>
          {data.map((_entry, index) => (
            <Cell key={`cell-${index}`} fill={getBarColor(index)} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  );
};

export default BarChartComponent;
