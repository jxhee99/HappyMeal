import React, { useState, useEffect } from 'react';
import { Container, Grid, Typography, Box, Button, CircularProgress, Tab, Tabs, useMediaQuery, useTheme } from '@mui/material';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';
import { foodService } from '../services/foodService';
import FoodCard from '../components/FoodCard';
import { useNavigate } from 'react-router-dom';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import PeopleIcon from '@mui/icons-material/People';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';

const MotionBox = motion(Box);

const HeroSection = styled(Box)(({ theme }) => ({
  textAlign: 'center',
  padding: theme.spacing(12, 3),
  background: 'linear-gradient(135deg, rgba(255, 107, 107, 0.1), rgba(255, 142, 83, 0.1))',
  borderRadius: '32px',
  marginBottom: theme.spacing(8),
  position: 'relative',
  overflow: 'hidden',
  '&::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'url("https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&q=80&w=2000") center/cover',
    opacity: 0.1,
    zIndex: 0,
  },
}));

const StatsSection = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'space-around',
  flexWrap: 'wrap',
  gap: theme.spacing(4),
  marginBottom: theme.spacing(8),
  padding: theme.spacing(4),
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  borderRadius: '24px',
  backdropFilter: 'blur(10px)',
}));

const StatItem = styled(Box)(({ theme }) => ({
  textAlign: 'center',
  padding: theme.spacing(3),
  minWidth: '200px',
}));

const CategorySection = styled(Box)(({ theme }) => ({
  marginBottom: theme.spacing(8),
  padding: theme.spacing(4),
  borderRadius: '24px',
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  backdropFilter: 'blur(10px)',
}));

const StyledTabs = styled(Tabs)(({ theme }) => ({
  marginBottom: theme.spacing(4),
  '& .MuiTabs-indicator': {
    backgroundColor: '#FF6B6B',
    height: 3,
  },
}));

const StyledTab = styled(Tab)(({ theme }) => ({
  textTransform: 'none',
  fontWeight: 600,
  fontSize: '1.1rem',
  minWidth: 100,
  '&.Mui-selected': {
    color: '#FF6B6B',
  },
}));

const Home = () => {
  const [categoryFoods, setCategoryFoods] = useState({
    diet: [],
    healthy: [],
    'bulk-up': [],
    cheating: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedTab, setSelectedTab] = useState(0);
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const categoryInfo = {
    diet: {
      title: '다이어트 식단',
      description: '저칼로리, 고단백 식단으로 건강한 다이어트를 도와드립니다',
      gradient: 'linear-gradient(135deg, #4CAF50, #8BC34A)'
    },
    healthy: {
      title: '건강한 식단',
      description: '균형 잡힌 영양소로 건강한 식습관을 만들어보세요',
      gradient: 'linear-gradient(135deg, #2196F3, #03A9F4)'
    },
    'bulk-up': {
      title: '근육 증량 식단',
      description: '고칼로리, 고단백 식단으로 근육을 키워보세요',
      gradient: 'linear-gradient(135deg, #FF9800, #FFC107)'
    },
    cheating: {
      title: '치팅데이 식단',
      description: '가끔은 맛있는 음식으로 기분 전환을 해보세요',
      gradient: 'linear-gradient(135deg, #E91E63, #FF4081)'
    }
  };

  const stats = [
    { icon: <RestaurantIcon sx={{ fontSize: 40 }} />, value: '10,000+', label: '푸드 데이터' },
    { icon: <PeopleIcon sx={{ fontSize: 40 }} />, value: '1,000,000+', label: '이용자 수' },
    { icon: <TrendingUpIcon sx={{ fontSize: 40 }} />, value: '500,000+', label: '하루 접속량' },
  ];

  useEffect(() => {
    const fetchAllCategoryFoods = async () => {
      try {
        setLoading(true);
        const categories = Object.keys(categoryFoods);
        const results = await Promise.all(
          categories.map(category => foodService.getRecommendedFoods(category))
        );
        
        const newCategoryFoods = {};
        categories.forEach((category, index) => {
          newCategoryFoods[category] = results[index] || [];
        });
        
        setCategoryFoods(newCategoryFoods);
      } catch (err) {
        setError('추천 음식을 불러오는데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchAllCategoryFoods();
  }, []);

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, textAlign: 'center' }}>
        <Typography color="error">{error}</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 8 }}>
      <MotionBox
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <HeroSection>
          <Typography
            variant={isMobile ? "h4" : "h2"}
            sx={{
              fontFamily: 'Noto Sans KR',
              fontWeight: 800,
              mb: 3,
              background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              position: 'relative',
              zIndex: 1,
            }}
          >
            건강한 식습관의 시작
          </Typography>
          <Typography
            variant={isMobile ? "subtitle1" : "h6"}
            sx={{
              fontFamily: 'Noto Sans KR',
              color: 'text.secondary',
              mb: 4,
              position: 'relative',
              zIndex: 1,
            }}
          >
            균형 잡힌 영양소와 맛있는 요리로 건강한 식습관을 만들어보세요
          </Typography>
          <Button
            variant="contained"
            size="large"
            sx={{
              background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
              color: 'white',
              padding: '12px 32px',
              borderRadius: '12px',
              fontFamily: 'Noto Sans KR',
              fontWeight: 600,
              position: 'relative',
              zIndex: 1,
              '&:hover': {
                background: 'linear-gradient(135deg, #FF8E53, #FF6B6B)',
              },
            }}
            onClick={() => navigate('/meallogs')}
          >
            식단 기록하기
          </Button>
        </HeroSection>

        <StatsSection>
          {stats.map((stat, index) => (
            <StatItem key={index}>
              <Box sx={{ color: '#FF6B6B', mb: 1 }}>{stat.icon}</Box>
              <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
                {stat.value}
              </Typography>
              <Typography variant="subtitle1" color="text.secondary">
                {stat.label}
              </Typography>
            </StatItem>
          ))}
        </StatsSection>

        <StyledTabs
          value={selectedTab}
          onChange={handleTabChange}
          variant={isMobile ? "scrollable" : "fullWidth"}
          scrollButtons={isMobile ? "auto" : false}
          centered={!isMobile}
        >
          {Object.entries(categoryInfo).map(([key, info], index) => (
            <StyledTab key={key} label={info.title} />
          ))}
        </StyledTabs>

        {Object.entries(categoryFoods).map(([category, foods], index) => (
          selectedTab === index && (
            <CategorySection key={category}>
              <Typography
                variant="h4"
                sx={{
                  fontFamily: 'Noto Sans KR',
                  fontWeight: 700,
                  mb: 2,
                  background: categoryInfo[category].gradient,
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                }}
              >
                {categoryInfo[category].title}
              </Typography>
              <Typography
                variant="subtitle1"
                sx={{
                  fontFamily: 'Noto Sans KR',
                  color: 'text.secondary',
                  mb: 4,
                }}
              >
                {categoryInfo[category].description}
              </Typography>
              <Grid container spacing={4}>
                {foods.map((food) => (
                  <Grid item xs={12} sm={6} md={3} key={food.foodId}>
                    <FoodCard 
                      food={food} 
                      onClick={() => {
                        console.log('Food clicked:', food);
                      }}
                    />
                  </Grid>
                ))}
              </Grid>
            </CategorySection>
          )
        ))}
      </MotionBox>
    </Container>
  );
};

export default Home; 