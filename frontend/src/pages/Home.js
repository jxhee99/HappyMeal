import React, { useState, useEffect } from 'react';
import { Container, Grid, Typography, Box, Button, CircularProgress } from '@mui/material';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';
import { foodService } from '../services/foodService';
import FoodCard from '../components/FoodCard';

const MotionBox = motion(Box);

const StyledCard = styled(Box)(({ theme }) => ({
  padding: '24px',
  borderRadius: '16px',
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  backdropFilter: 'blur(10px)',
  border: '1px solid rgba(255, 255, 255, 0.1)',
  transition: 'all 0.3s ease',
  cursor: 'pointer',
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: '0 8px 24px rgba(0, 0, 0, 0.1)',
  },
}));

const CategoryTitle = styled(Typography)(({ theme }) => ({
  fontFamily: 'Noto Sans KR',
  fontWeight: 700,
  fontSize: '1.5rem',
  marginBottom: '16px',
  background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
  WebkitBackgroundClip: 'text',
  WebkitTextFillColor: 'transparent',
}));

const MealImage = styled('img')({
  width: '100%',
  height: '200px',
  objectFit: 'cover',
  borderRadius: '12px',
  marginBottom: '16px',
});

const MealTitle = styled(Typography)({
  fontFamily: 'Noto Sans KR',
  fontWeight: 600,
  fontSize: '1.1rem',
  marginBottom: '8px',
});

const MealDescription = styled(Typography)({
  fontFamily: 'Noto Sans KR',
  color: 'rgba(0, 0, 0, 0.6)',
  fontSize: '0.9rem',
  marginBottom: '16px',
});

const NutritionInfo = styled(Box)({
  display: 'flex',
  gap: '8px',
  marginBottom: '16px',
});

const NutritionChip = styled(Box)(({ color }) => ({
  padding: '4px 12px',
  borderRadius: '16px',
  fontSize: '0.8rem',
  fontFamily: 'Noto Sans KR',
  fontWeight: 500,
  background: `linear-gradient(135deg, ${color}20, ${color}10)`,
  color: color,
}));

const CategorySection = styled(Box)(({ theme }) => ({
  marginBottom: '48px',
  padding: '24px',
  borderRadius: '24px',
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  backdropFilter: 'blur(10px)',
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
          newCategoryFoods[category] = results[index];
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
        <Box
          sx={{
            textAlign: 'center',
            mb: 8,
            background: 'linear-gradient(135deg, rgba(255, 107, 107, 0.1), rgba(255, 142, 83, 0.1))',
            padding: '48px 24px',
            borderRadius: '24px',
            backdropFilter: 'blur(10px)',
          }}
        >
          <Typography
            variant="h3"
            sx={{
              fontFamily: 'Noto Sans KR',
              fontWeight: 700,
              mb: 2,
              background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            오늘의 건강한 식단 추천
          </Typography>
          <Typography
            variant="h6"
            sx={{
              fontFamily: 'Noto Sans KR',
              color: 'text.secondary',
              mb: 4,
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
              '&:hover': {
                background: 'linear-gradient(135deg, #FF8E53, #FF6B6B)',
              },
            }}
          >
            식단 기록하기
          </Button>
        </Box>

        {Object.entries(categoryFoods).map(([category, foods]) => (
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
                      // 상세 정보 보기 클릭 시 처리
                      console.log('Food clicked:', food);
                    }}
                  />
                </Grid>
              ))}
            </Grid>
          </CategorySection>
        ))}
      </MotionBox>
    </Container>
  );
};

export default Home; 