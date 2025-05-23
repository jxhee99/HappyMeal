import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Box, Container, Grid, Paper, CircularProgress, Typography, useTheme, useMediaQuery } from '@mui/material';
import { styled } from '@mui/material/styles';
import { motion } from 'framer-motion';
import NutritionSummary from '../components/meallogs/NutritionSummary';
import RecentMeals from '../components/meallogs/RecentMeals';
import MealLogCalendar from '../components/meallogs/MealLogCalendar';
import { mealLogService } from '../services/mealLogService';
import { format } from 'date-fns';
import RestaurantMenuIcon from '@mui/icons-material/RestaurantMenu';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import BarChartIcon from '@mui/icons-material/BarChart';

const MotionBox = motion(Box);

const StyledPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(3),
  height: '100%',
  borderRadius: '24px',
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  backdropFilter: 'blur(10px)',
  border: '1px solid rgba(255, 255, 255, 0.1)',
  transition: 'all 0.3s ease',
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: '0 8px 24px rgba(0, 0, 0, 0.1)',
  },
}));

const SectionTitle = styled(Box)(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  gap: theme.spacing(1),
  marginBottom: theme.spacing(3),
  paddingBottom: theme.spacing(2),
  borderBottom: '1px solid rgba(0, 0, 0, 0.1)',
}));

const LoadingContainer = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  minHeight: 400,
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  borderRadius: '16px',
}));

const MealLogs = () => {
  const auth = useAuth();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
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
      <MotionBox
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Typography
          variant={isMobile ? "h4" : "h3"}
          sx={{
            fontFamily: 'Noto Sans KR',
            fontWeight: 700,
            mb: 4,
            textAlign: 'center',
            background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
          }}
        >
          나의 식단 관리
        </Typography>

        <Grid container spacing={3} sx={{ position: 'relative', minHeight: '100vh' }}>
          {/* 식단 기록 캘린더 */}
          <Grid item xs={12} md={4}>
            <StyledPaper elevation={3} sx={{ height: '100%' }}>
              <SectionTitle>
                <CalendarMonthIcon sx={{ color: '#FF6B6B', fontSize: 28 }} />
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  식단 캘린더
                </Typography>
              </SectionTitle>
              <Box sx={{ 
                position: { md: 'sticky' },
                top: { md: '80px' }
              }}>
                <MealLogCalendar onDateSelect={handleDateSelect} />
              </Box>
            </StyledPaper>
          </Grid>

          {/* 식단 기록 목록 */}
          <Grid item xs={12} md={4}>
            <StyledPaper elevation={3}>
              <SectionTitle>
                <RestaurantMenuIcon sx={{ color: '#FF6B6B', fontSize: 28 }} />
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  오늘의 식단
                </Typography>
              </SectionTitle>
              {loading ? (
                <LoadingContainer>
                  <CircularProgress />
                </LoadingContainer>
              ) : (
                <RecentMeals 
                  selectedDate={selectedDate} 
                  mealLogs={mealLogs} 
                  onMealLogAdded={handleMealLogAdded}
                />
              )}
            </StyledPaper>
          </Grid>

          {/* 영양소 통계 */}
          <Grid item xs={12} md={4}>
            <StyledPaper elevation={3} sx={{ height: '100%' }}>
              <SectionTitle>
                <BarChartIcon sx={{ color: '#FF6B6B', fontSize: 28 }} />
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  영양소 분석
                </Typography>
              </SectionTitle>
              <Box sx={{ 
                position: { md: 'sticky' },
                top: { md: '80px' }
              }}>
                <NutritionSummary
                  selectedDate={selectedDate}
                  dailyStats={dailyStats}
                  weeklyStats={weeklyStats}
                />
              </Box>
            </StyledPaper>
          </Grid>
        </Grid>
      </MotionBox>
    </Container>
  );
};

export default MealLogs; 