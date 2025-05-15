import React from 'react';
import { Line } from 'react-chartjs-2';
import { Box, useTheme } from '@mui/material';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

const WeeklyBalanceChart = () => {
  const theme = useTheme();

  const data = {
    labels: ['월', '화', '수', '목', '금', '토', '일'],
    datasets: [
      {
        label: '칼로리',
        data: [2100, 1900, 2200, 1800, 2000, 2300, 1900],
        borderColor: '#FF6B6B',
        backgroundColor: 'rgba(255, 107, 107, 0.1)',
        tension: 0.4,
        fill: true,
        pointBackgroundColor: '#FF6B6B',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6,
      },
      {
        label: '단백질',
        data: [80, 75, 85, 70, 78, 82, 75],
        borderColor: '#FF8E53',
        backgroundColor: 'rgba(255, 142, 83, 0.1)',
        tension: 0.4,
        fill: true,
        pointBackgroundColor: '#FF8E53',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: {
          font: {
            family: 'Noto Sans KR',
            size: 12,
            weight: 500,
          },
          padding: 20,
          usePointStyle: true,
          pointStyle: 'circle',
        },
      },
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        titleColor: '#2d3436',
        bodyColor: '#2d3436',
        borderColor: 'rgba(0, 0, 0, 0.1)',
        borderWidth: 1,
        padding: 12,
        boxPadding: 6,
        usePointStyle: true,
        titleFont: {
          family: 'Noto Sans KR',
          size: 14,
          weight: 700,
        },
        bodyFont: {
          family: 'Noto Sans KR',
          size: 13,
        },
        callbacks: {
          label: function(context) {
            let label = context.dataset.label || '';
            if (label) {
              label += ': ';
            }
            if (context.parsed.y !== null) {
              label += context.parsed.y + (label.includes('칼로리') ? ' kcal' : ' g');
            }
            return label;
          }
        }
      },
    },
    scales: {
      x: {
        grid: {
          display: false,
        },
        ticks: {
          font: {
            family: 'Noto Sans KR',
            size: 12,
            weight: 500,
          },
          color: '#636e72',
        },
      },
      y: {
        grid: {
          color: 'rgba(0, 0, 0, 0.05)',
          drawBorder: false,
        },
        ticks: {
          font: {
            family: 'Noto Sans KR',
            size: 12,
            weight: 500,
          },
          color: '#636e72',
          padding: 10,
        },
      },
    },
    interaction: {
      intersect: false,
      mode: 'index',
    },
  };

  return (
    <Box sx={{ position: 'relative', width: '100%', height: '100%' }}>
      <Line data={data} options={options} />
    </Box>
  );
};

export default WeeklyBalanceChart; 