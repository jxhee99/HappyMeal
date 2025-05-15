import React from 'react';
import { Box, Paper, Typography, Avatar, Chip } from '@mui/material';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';

const MotionPaper = motion(Paper);

const MealCard = styled(Box)(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  padding: '16px',
  marginBottom: '16px',
  borderRadius: '12px',
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  backdropFilter: 'blur(10px)',
  border: '1px solid rgba(255, 255, 255, 0.1)',
  transition: 'all 0.3s ease',
  '&:hover': {
    transform: 'translateY(-2px)',
    background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.1))',
  },
  '& .meal-info': {
    flex: 1,
    marginLeft: '16px',
  },
  '& .meal-title': {
    fontFamily: 'Noto Sans KR',
    fontWeight: 600,
    fontSize: '1rem',
    marginBottom: '4px',
  },
  '& .meal-time': {
    fontFamily: 'Noto Sans KR',
    color: theme.palette.text.secondary,
    fontSize: '0.875rem',
  },
  '& .meal-nutrition': {
    display: 'flex',
    gap: '8px',
    marginTop: '8px',
  },
}));

const StyledChip = styled(Chip)(({ theme }) => ({
  fontFamily: 'Noto Sans KR',
  fontSize: '0.75rem',
  height: '24px',
  '& .MuiChip-label': {
    padding: '0 8px',
  },
}));

const RecentMeals = () => {
  const meals = [
    {
      id: 1,
      name: '닭가슴살 샐러드',
      time: '12:30',
      calories: 350,
      protein: 30,
      image: 'https://source.unsplash.com/random/100x100/?salad',
    },
    {
      id: 2,
      name: '연어 스테이크',
      time: '19:00',
      calories: 450,
      protein: 35,
      image: 'https://source.unsplash.com/random/100x100/?salmon',
    },
    {
      id: 3,
      name: '그릭 요거트',
      time: '15:00',
      calories: 200,
      protein: 15,
      image: 'https://source.unsplash.com/random/100x100/?yogurt',
    },
  ];

  return (
    <MotionPaper
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      sx={{
        p: 3,
        background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
        backdropFilter: 'blur(10px)',
        borderRadius: '16px',
        border: '1px solid rgba(255, 255, 255, 0.1)',
      }}
    >
      <Typography
        variant="h6"
        sx={{
          mb: 3,
          fontFamily: 'Noto Sans KR',
          fontWeight: 700,
          background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
        }}
      >
        최근 식사
      </Typography>
      <Box>
        {meals.map((meal) => (
          <motion.div
            key={meal.id}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.3, delay: meal.id * 0.1 }}
          >
            <MealCard>
              <Avatar
                src={meal.image}
                alt={meal.name}
                sx={{ width: 60, height: 60, borderRadius: '12px' }}
              />
              <Box className="meal-info">
                <Typography className="meal-title">{meal.name}</Typography>
                <Typography className="meal-time">{meal.time}</Typography>
                <Box className="meal-nutrition">
                  <StyledChip
                    label={`${meal.calories} kcal`}
                    size="small"
                    sx={{
                      background: 'linear-gradient(135deg, rgba(255, 107, 107, 0.2), rgba(255, 142, 83, 0.2))',
                      color: '#FF6B6B',
                    }}
                  />
                  <StyledChip
                    label={`단백질 ${meal.protein}g`}
                    size="small"
                    sx={{
                      background: 'linear-gradient(135deg, rgba(255, 142, 83, 0.2), rgba(255, 179, 71, 0.2))',
                      color: '#FF8E53',
                    }}
                  />
                </Box>
              </Box>
            </MealCard>
          </motion.div>
        ))}
      </Box>
    </MotionPaper>
  );
};

export default RecentMeals; 