import React from 'react';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';

interface KPICardProps {
  title: string;
  value: string | number;
  change?: number;
  yoyChange?: number;
  prefix?: string;
  suffix?: string;
  subtitle?: string;
}

const KPICard: React.FC<KPICardProps> = ({ title, value, change, yoyChange, prefix = '', suffix = '', subtitle }) => {
  const displayChange = yoyChange !== undefined ? yoyChange : change;
  
  const getTrendIcon = () => {
    if (displayChange === undefined || displayChange === null) return null;
    if (displayChange > 0) return <TrendingUp className="w-5 h-5 text-green-500" />;
    if (displayChange < 0) return <TrendingDown className="w-5 h-5 text-red-500" />;
    return <Minus className="w-5 h-5 text-gray-400" />;
  };

  const getTrendColor = () => {
    if (displayChange === undefined || displayChange === null) return 'text-gray-600';
    if (displayChange > 0) return 'text-green-600';
    if (displayChange < 0) return 'text-red-600';
    return 'text-gray-600';
  };

  const formatValue = (val: string | number) => {
    if (typeof val === 'number') {
      return `${prefix}${val.toLocaleString()}${suffix}`;
    }
    return `${prefix}${val}${suffix}`;
  };

  return (
    <div className="card">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="mt-2 text-3xl font-semibold text-gray-900">{formatValue(value)}</p>
          {subtitle && (
            <p className="mt-1 text-sm text-gray-500">{subtitle}</p>
          )}
        </div>
        {displayChange !== undefined && displayChange !== null && (
          <div className="flex items-center space-x-1">
            {getTrendIcon()}
            <span className={`text-sm font-medium ${getTrendColor()}`}>
              {displayChange > 0 ? '+' : ''}{displayChange.toFixed(2)}%
            </span>
          </div>
        )}
      </div>
    </div>
  );
};

export default KPICard;
