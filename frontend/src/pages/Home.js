import React from 'react';
import { Container, Grid, Typography, Box, Button } from '@mui/material';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';

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

const Home = () => {
  const recommendedMeals = [
    {
      id: 1,
      title: '단백질 풍부한 닭가슴살 샐러드',
      description: '신선한 채소와 구운 닭가슴살로 만든 건강한 샐러드',
      image: 'https://source.unsplash.com/random/400x300/?salad',
      calories: 350,
      protein: 30,
      carbs: 15,
      fat: 12,
    },
    {
      id: 2,
      title: '지중해식 연어 스테이크',
      description: '올리브 오일과 허브로 맛을 낸 건강한 연어 요리',
      image: 'https://source.unsplash.com/random/400x300/?salmon',
      calories: 450,
      protein: 35,
      carbs: 20,
      fat: 25,
    },
    {
      id: 3,
      title: '퀴노아 보울',
      description: '슈퍼푸드 퀴노아와 신선한 채소로 만든 영양만점 보울',
      image: 'https://source.unsplash.com/random/400x300/?quinoa',
      calories: 380,
      protein: 15,
      carbs: 45,
      fat: 18,
    },
  ];

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

        <Grid container spacing={4}>
          {recommendedMeals.map((meal) => (
            <Grid item xs={12} md={4} key={meal.id}>
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: meal.id * 0.1 }}
              >
                <StyledCard>
                  <MealImage src={meal.image} alt={meal.title} />
                  <MealTitle>{meal.title}</MealTitle>
                  <MealDescription>{meal.description}</MealDescription>
                  <NutritionInfo>
                    <NutritionChip color="#FF6B6B">
                      {meal.calories} kcal
                    </NutritionChip>
                    <NutritionChip color="#FF8E53">
                      단백질 {meal.protein}g
                    </NutritionChip>
                    <NutritionChip color="#FFB347">
                      탄수화물 {meal.carbs}g
                    </NutritionChip>
                    <NutritionChip color="#FFD700">
                      지방 {meal.fat}g
                    </NutritionChip>
                  </NutritionInfo>
                  <Button
                    variant="outlined"
                    fullWidth
                    sx={{
                      borderColor: '#FF6B6B',
                      color: '#FF6B6B',
                      '&:hover': {
                        borderColor: '#FF8E53',
                        backgroundColor: 'rgba(255, 107, 107, 0.05)',
                      },
                    }}
                  >
                    상세 정보 보기
                  </Button>
                </StyledCard>
              </motion.div>
            </Grid>
          ))}
        </Grid>
      </MotionBox>
    </Container>
  );
};

export default Home; 