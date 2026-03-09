import React, { useState } from 'react';
import { Upload, CheckCircle, XCircle } from 'lucide-react';
import { analyticsService } from '../services/analyticsService';

const DataUploadPage: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      setFile(event.target.files[0]);
      setMessage(null);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage({ type: 'error', text: 'Please select a file first' });
      return;
    }

    setUploading(true);
    setMessage(null);

    try {
      const isCSV = file.name.endsWith('.csv');
      const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls');

      if (!isCSV && !isExcel) {
        throw new Error('Please upload a CSV or Excel file');
      }

      const result = isCSV ? await analyticsService.uploadCSV(file) : await analyticsService.uploadExcel(file);
      setMessage({ 
        type: 'success', 
        text: `File uploaded successfully! ${result.successCount} records processed, ${result.errorCount} errors.` 
      });
      setFile(null);
    } catch (error: any) {
      setMessage({ type: 'error', text: error.message || 'Upload failed' });
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Data Upload</h1>

      <div className="card">
        <div className="text-center">
          <Upload className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-semibold text-gray-900">Upload Data File</h3>
          <p className="mt-1 text-sm text-gray-500">CSV or Excel files supported</p>

          <div className="mt-6">
            <label htmlFor="file-upload" className="cursor-pointer">
              <span className="btn-primary inline-block">
                Select File
              </span>
              <input
                id="file-upload"
                name="file-upload"
                type="file"
                accept=".csv,.xlsx,.xls"
                className="sr-only"
                onChange={handleFileChange}
              />
            </label>
          </div>

          {file && (
            <div className="mt-4">
              <p className="text-sm text-gray-700">
                Selected: <span className="font-medium">{file.name}</span>
              </p>
              <button
                onClick={handleUpload}
                disabled={uploading}
                className="mt-4 btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {uploading ? 'Uploading...' : 'Upload'}
              </button>
            </div>
          )}

          {message && (
            <div className={`mt-4 p-4 rounded-lg ${
              message.type === 'success' ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'
            }`}>
              <div className="flex items-center">
                {message.type === 'success' ? (
                  <CheckCircle className="h-5 w-5 text-green-400 mr-2" />
                ) : (
                  <XCircle className="h-5 w-5 text-red-400 mr-2" />
                )}
                <p className={`text-sm ${message.type === 'success' ? 'text-green-800' : 'text-red-800'}`}>
                  {message.text}
                </p>
              </div>
            </div>
          )}
        </div>

        <div className="mt-8 border-t border-gray-200 pt-6">
          <h4 className="text-sm font-semibold text-gray-900 mb-2">File Format Requirements:</h4>
          <ul className="text-sm text-gray-600 space-y-1 list-disc list-inside">
            <li>CSV or Excel (.xlsx, .xls) format</li>
            <li>Maximum file size: 10MB</li>
            <li>Required columns: product, store, date, quantity, value</li>
            <li>Date format: YYYY-MM-DD</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default DataUploadPage;
