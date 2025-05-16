import React, { useState } from 'react';
import { Box, Typography, Grid, Tabs, Tab } from '@mui/material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { format, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <Box sx={{ 
        bgcolor: 'background.paper', 
        p: 2, 
        border: '1px solid #ccc',
        borderRadius: 1
      }}>
        <Typography variant="subtitle2" sx={{ mb: 1 }}>
          {format(parseISO(label), 'M월 d일')}
        </Typography>
        {payload.map((entry) => (
          <Typography
            key={entry.name}
            variant="body2"
            sx={{ color: entry.color, mb: 0.5 }}
          >
            {entry.name}: {Number(entry.value).toFixed(1)} {entry.name.includes('칼로리') ? 'kcal' : 'g'}
          </Typography>
        ))}
      </Box>
    );
  }
  return null;
};

const NutritionSummary = ({ selectedDate, dailyStats, weeklyStats }) => {
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  if (!dailyStats && !weeklyStats) {
    return (
      <Box p={3}>
        <Typography>영양소 정보가 없습니다.</Typography>
      </Box>
    );
  }

  const renderDailyStats = () => {
    if (!dailyStats) {
      return (
        <Box p={3}>
          <Typography>선택한 날짜의 영양소 정보가 없습니다.</Typography>
        </Box>
      );
    }

    return (
      <Grid container spacing={2}>
        <Grid item xs={6}>
          <Box p={2} bgcolor="#f5f5f5" borderRadius={2}>
            <Typography variant="subtitle2" color="textSecondary">
              총 칼로리
            </Typography>
            <Typography variant="h6" color="primary">
              {Number(dailyStats.totalCalories || 0).toFixed(1)} kcal
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={6}>
          <Box p={2} bgcolor="#f5f5f5" borderRadius={2}>
            <Typography variant="subtitle2" color="textSecondary">
              단백질
            </Typography>
            <Typography variant="h6" color="primary">
              {Number(dailyStats.totalProtein || 0).toFixed(1)}g
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={6}>
          <Box p={2} bgcolor="#f5f5f5" borderRadius={2}>
            <Typography variant="subtitle2" color="textSecondary">
              탄수화물
            </Typography>
            <Typography variant="h6" color="primary">
              {Number(dailyStats.totalCarbs || 0).toFixed(1)}g
            </Typography>
            <Typography variant="caption" color="textSecondary">
              당류: {Number(dailyStats.totalSugar || 0).toFixed(1)}g
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={6}>
          <Box p={2} bgcolor="#f5f5f5" borderRadius={2}>
            <Typography variant="subtitle2" color="textSecondary">
              지방
            </Typography>
            <Typography variant="h6" color="primary">
              {Number(dailyStats.totalFat || 0).toFixed(1)}g
            </Typography>
          </Box>
        </Grid>
      </Grid>
    );
  };

  const renderWeeklyStats = () => {
    if (!weeklyStats || weeklyStats.length === 0) {
      return (
        <Box p={3}>
          <Typography>주간 통계 데이터가 없습니다.</Typography>
        </Box>
      );
    }

    // 데이터가 없는 날짜도 0으로 표시되도록 데이터 가공
    const processedData = weeklyStats.map(stat => ({
      date: stat.date,
      totalCalories: Number(stat.totalCalories || 0),
      totalProtein: Number(stat.totalProtein || 0),
      totalCarbs: Number(stat.totalCarbs || 0),
      totalFat: Number(stat.totalFat || 0),
      totalSugar: Number(stat.totalSugar || 0)
    }));

    return (
      <Box sx={{ width: '100%', height: 400 }}>
        <ResponsiveContainer>
          <LineChart data={processedData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis 
              dataKey="date" 
              tickFormatter={(date) => format(parseISO(date), 'M/d')}
            />
            <YAxis />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            <Line 
              type="monotone" 
              dataKey="totalCalories" 
              name="칼로리" 
              stroke="#FF6B6B" 
              strokeWidth={2}
              dot={{ r: 4 }}
              connectNulls
            />
            <Line 
              type="monotone" 
              dataKey="totalProtein" 
              name="단백질" 
              stroke="#4CAF50" 
              strokeWidth={2}
              dot={{ r: 4 }}
              connectNulls
            />
            <Line 
              type="monotone" 
              dataKey="totalCarbs" 
              name="탄수화물" 
              stroke="#2196F3" 
              strokeWidth={2}
              dot={{ r: 4 }}
              connectNulls
            />
            <Line 
              type="monotone" 
              dataKey="totalFat" 
              name="지방" 
              stroke="#FFC107" 
              strokeWidth={2}
              dot={{ r: 4 }}
              connectNulls
            />
          </LineChart>
        </ResponsiveContainer>
      </Box>
    );
  };

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        영양소 통계
      </Typography>
      <Tabs value={tabValue} onChange={handleTabChange} sx={{ mb: 2 }}>
        <Tab label="일일 통계" />
        <Tab label="주간 통계" />
      </Tabs>
      {tabValue === 0 ? renderDailyStats() : renderWeeklyStats()}
    </Box>
  );
};

export default NutritionSummary; 