import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Box, Container, Grid, Paper, CircularProgress } from '@mui/material';
import NutritionSummary from '../components/meallogs/NutritionSummary';
import RecentMeals from '../components/meallogs/RecentMeals';
import MealLogCalendar from '../components/meallogs/MealLogCalendar';
import { mealLogService } from '../services/mealLogService';
import { format } from 'date-fns';

const MealLogs = () => {
  const auth = useAuth();
  const [selectedDate, setSelectedDate] = useState(format(new Date(), 'yyyy-MM-dd'));
  const [mealLogs, setMealLogs] = useState([]);
  const [dailyStats, setDailyStats] = useState(null);
  const [weeklyStats, setWeeklyStats] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = async (date) => {
    try {
      // 데이터 페칭 전에 기존 데이터 초기화
      setMealLogs([]);
      setDailyStats(null);
      setWeeklyStats([]);
      setError(null);
      setLoading(true);

      console.log('데이터 요청 시작:', date);

      // 병렬로 데이터 요청
      const [logsResponse, statsResponse, weeklyStatsResponse] = await Promise.allSettled([
        mealLogService.getMealLogsByDate(date),
        mealLogService.getMealLogStatsByDate(date),
        mealLogService.getWeeklyMealLogs(date)
      ]);

      // 각 응답 처리
      if (logsResponse.status === 'fulfilled') {
        console.log('식단 기록 응답:', logsResponse.value);
        setMealLogs(logsResponse.value || []);
      } else {
        console.error('식단 기록 조회 실패:', logsResponse.reason);
        setError('식단 기록을 불러오는데 실패했습니다.');
      }

      if (statsResponse.status === 'fulfilled') {
        console.log('일일 통계 응답:', statsResponse.value);
        setDailyStats(statsResponse.value || null);
      } else {
        console.error('일일 통계 조회 실패:', statsResponse.reason);
        setError('일일 통계를 불러오는데 실패했습니다.');
      }

      if (weeklyStatsResponse.status === 'fulfilled') {
        console.log('주간 통계 응답:', weeklyStatsResponse.value);
        setWeeklyStats(weeklyStatsResponse.value || []);
      } else {
        console.error('주간 통계 조회 실패:', weeklyStatsResponse.reason);
        setError('주간 통계를 불러오는데 실패했습니다.');
      }

    } catch (err) {
      console.error('데이터 요청 중 에러 발생:', err);
      setError('데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 날짜가 변경될 때마다 데이터 새로 불러오기
  useEffect(() => {
    fetchData(selectedDate);
  }, [selectedDate]);

  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

  const handleMealLogAdded = () => {
    fetchData(selectedDate);
  };

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Grid container spacing={3}>
        {/* 식단 기록 캘린더 */}
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ height: '100%' }}>
            <MealLogCalendar onDateSelect={handleDateSelect} />
          </Paper>
        </Grid>

        {/* 식단 기록 목록 */}
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 3, height: '100%', position: 'relative' }}>
            {loading ? (
              <Box display="flex" justifyContent="center" alignItems="center" minHeight={400}>
                <CircularProgress />
              </Box>
            ) : (
              <RecentMeals 
                selectedDate={selectedDate} 
                mealLogs={mealLogs} 
                onMealLogAdded={handleMealLogAdded}
              />
            )}
          </Paper>
        </Grid>

        {/* 영양소 통계 */}
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
            <NutritionSummary
              selectedDate={selectedDate}
              dailyStats={dailyStats}
              weeklyStats={weeklyStats}
            />
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default MealLogs; 