import React, { useState, useEffect } from 'react';
import { Box, Typography, CircularProgress, Grid } from '@mui/material';
import { mealLogService } from '../../services/mealLogService';

const NutritionSummary = () => {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        setLoading(true);
        const data = await mealLogService.getTodayNutritionSummary();
        setSummary(data);
      } catch (err) {
        setError('영양소 요약을 불러오는데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchSummary();
  }, []);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box p={3}>
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  if (!summary) {
    return (
      <Box p={3}>
        <Typography>오늘의 식단 기록이 없습니다.</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        오늘의 영양소 요약
      </Typography>
      <Grid container spacing={2}>
        <Grid item xs={6}>
          <Box p={2} bgcolor="#f5f5f5" borderRadius={2}>
            <Typography variant="subtitle2" color="textSecondary">
              총 칼로리
            </Typography>
            <Typography variant="h6" color="primary">
              {summary.totalCalories} kcal
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={6}>
          <Box p={2} bgcolor="#f5f5f5" borderRadius={2}>
            <Typography variant="subtitle2" color="textSecondary">
              단백질
            </Typography>
            <Typography variant="h6" color="primary">
              {summary.totalProtein}g
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={6}>
          <Box p={2} bgcolor="#f5f5f5" borderRadius={2}>
            <Typography variant="subtitle2" color="textSecondary">
              탄수화물
            </Typography>
            <Typography variant="h6" color="primary">
              {summary.totalCarbs}g
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={6}>
          <Box p={2} bgcolor="#f5f5f5" borderRadius={2}>
            <Typography variant="subtitle2" color="textSecondary">
              지방
            </Typography>
            <Typography variant="h6" color="primary">
              {summary.totalFat}g
            </Typography>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

export default NutritionSummary; 