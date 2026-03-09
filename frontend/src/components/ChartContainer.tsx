import React, { ReactNode } from 'react';
import { Loader2 } from 'lucide-react';

interface ChartContainerProps {
  title: string;
  children: ReactNode;
  loading?: boolean;
}

const ChartContainer: React.FC<ChartContainerProps> = ({ title, children, loading = false }) => {
  return (
    <div className="card h-full">
      <h3 className="text-lg font-semibold text-gray-800 mb-4">
        {title}
      </h3>
      {loading ? (
        <div className="flex justify-center items-center min-h-[300px]">
          <Loader2 className="w-8 h-8 text-primary animate-spin" />
        </div>
      ) : (
        <div className="mt-2">{children}</div>
      )}
    </div>
  );
};

export default ChartContainer;
