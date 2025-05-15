import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Box, Container, Grid, Paper, Typography } from '@mui/material';
import NutritionSummary from '../components/meallogs/NutritionSummary';
import RecentMeals from '../components/meallogs/RecentMeals';
import WeeklyBalanceChart from '../components/meallogs/WeeklyBalanceChart';
import MealLogCalendar from '../components/meallogs/MealLogCalendar';

const MealLogs = () => {
  const auth = useAuth();

  if (!auth.isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom sx={{ mb: 4 }}>
        식단 기록
      </Typography>
      <Grid container spacing={3}>
        {/* 식단 기록 캘린더 */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
            <MealLogCalendar />
          </Paper>
        </Grid>

        {/* 주간 영양소 밸런스 차트 */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
            <WeeklyBalanceChart />
          </Paper>
        </Grid>

        {/* 식단 기록 */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
            <RecentMeals />
          </Paper>
        </Grid>

        {/* 오늘의 영양소 요약 */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
            <NutritionSummary />
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default MealLogs; 