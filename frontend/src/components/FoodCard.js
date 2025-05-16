import React, { useState } from "react";
import { Box, Typography, Button } from "@mui/material";
import { styled } from '@mui/material/styles';
import { motion } from 'framer-motion';

const MotionBox = motion(Box);

const DEFAULT_FOOD_IMAGE = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&q=80&w=400&h=300";

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

const MealImage = styled('img')({
  width: '100%',
  height: '200px',
  objectFit: 'cover',
  borderRadius: '12px',
  marginBottom: '16px',
  backgroundColor: '#f5f5f5',
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
  flexWrap: 'wrap',
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

function FoodCard({ food, onClick }) {
  const [imgSrc, setImgSrc] = useState(food.imgUrl || DEFAULT_FOOD_IMAGE);

  const handleImageError = () => {
    setImgSrc(DEFAULT_FOOD_IMAGE);
  };

  return (
    <MotionBox
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <StyledCard onClick={onClick}>
        <MealImage 
          src={imgSrc}
          alt={food.name}
          onError={handleImageError}
        />
        <MealTitle>{food.name}</MealTitle>
        <MealDescription>{food.category}</MealDescription>
        <NutritionInfo>
          <NutritionChip color="#FF6B6B">
            {food.calories} kcal
          </NutritionChip>
          <NutritionChip color="#FF8E53">
            단백질 {food.protein}g
          </NutritionChip>
          <NutritionChip color="#FFB347">
            탄수화물 {food.carbs}g
          </NutritionChip>
          <NutritionChip color="#FFD700">
            지방 {food.fat}g
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
    </MotionBox>
  );
}

export default FoodCard; 