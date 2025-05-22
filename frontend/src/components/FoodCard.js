import React, { useState } from "react";
import { Box, Typography, Button, Modal, IconButton, useTheme, useMediaQuery, Grid } from "@mui/material";
import { styled } from '@mui/material/styles';
import { motion, AnimatePresence } from 'framer-motion';
import AddIcon from '@mui/icons-material/Add';
import CloseIcon from '@mui/icons-material/Close';

const MotionBox = motion(Box);

const DEFAULT_FOOD_IMAGE = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&q=80&w=400&h=300";

const StyledCard = styled(Box)(({ theme }) => ({
  width: '100%',
  minHeight: '420px',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  padding: '24px',
  borderRadius: '16px',
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  backdropFilter: 'blur(10px)',
  border: '1px solid rgba(255, 255, 255, 0.1)',
  transition: 'all 0.3s ease',
  cursor: 'pointer',
  boxSizing: 'border-box',
  position: 'relative',
  overflow: 'hidden',
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: '0 8px 24px rgba(0, 0, 0, 0.1)',
  },
}));

const MealImage = styled('img')({
  width: '100%',
  height: '180px',
  objectFit: 'cover',
  borderRadius: '12px',
  marginBottom: '16px',
  backgroundColor: '#f5f5f5',
  transition: 'all 0.3s ease',
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

const AddButton = styled(IconButton)(({ theme }) => ({
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  backgroundColor: 'rgba(255, 255, 255, 0.9)',
  width: '48px',
  height: '48px',
  borderRadius: '50%',
  opacity: 0,
  transition: 'all 0.3s ease',
  '&:hover': {
    backgroundColor: '#FF6B6B',
    color: 'white',
  },
}));

const ModalContent = styled(Box)(({ theme }) => ({
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: '90%',
  maxWidth: '800px',
  maxHeight: '90vh',
  backgroundColor: 'white',
  borderRadius: '24px',
  padding: theme.spacing(4),
  overflow: 'auto',
  outline: 'none',
}));

const ModalImage = styled('img')({
  width: '100%',
  height: '300px',
  objectFit: 'cover',
  borderRadius: '16px',
  marginBottom: '24px',
});

const CloseButton = styled(IconButton)(({ theme }) => ({
  position: 'absolute',
  top: theme.spacing(2),
  right: theme.spacing(2),
  color: 'rgba(0, 0, 0, 0.6)',
  '&:hover': {
    color: '#FF6B6B',
  },
}));

function FoodCard({ food, onClick }) {
  const [imgSrc, setImgSrc] = useState(food.imgUrl || DEFAULT_FOOD_IMAGE);
  const [hovered, setHovered] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const handleImageError = () => {
    setImgSrc(DEFAULT_FOOD_IMAGE);
  };

  const handleCardClick = (e) => {
    e.stopPropagation();
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
  };

  return (
    <>
      <MotionBox
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <StyledCard
          onClick={handleCardClick}
          onMouseEnter={() => setHovered(true)}
          onMouseLeave={() => setHovered(false)}
        >
          <MealImage 
            src={imgSrc}
            alt={food.name}
            onError={handleImageError}
            sx={{
              filter: hovered ? 'brightness(0.8)' : 'brightness(1)',
            }}
          />
          <AddButton
            sx={{
              opacity: hovered ? 1 : 0,
            }}
          >
            <AddIcon />
          </AddButton>
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
        </StyledCard>
      </MotionBox>

      <Modal
        open={modalOpen}
        onClose={handleCloseModal}
        aria-labelledby="food-detail-modal"
      >
        <ModalContent>
          <CloseButton onClick={handleCloseModal}>
            <CloseIcon />
          </CloseButton>
          <ModalImage src={imgSrc} alt={food.name} />
          <Typography variant="h4" sx={{ mb: 2, fontWeight: 700 }}>
            {food.name}
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" sx={{ mb: 3 }}>
            {food.category}
          </Typography>
          <Box>
            <Typography variant="h6" sx={{ mb: 2 }}>영양 정보</Typography>
            <Box sx={{ 
              display: 'flex', 
              flexDirection: 'column', 
              gap: 2,
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
              padding: 3,
              borderRadius: 2
            }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography>칼로리</Typography>
                <Typography fontWeight={600}>{food.calories} kcal</Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography>단백질</Typography>
                <Typography fontWeight={600}>{food.protein}g</Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography>탄수화물</Typography>
                <Typography fontWeight={600}>{food.carbs}g</Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography>지방</Typography>
                <Typography fontWeight={600}>{food.fat}g</Typography>
              </Box>
            </Box>
          </Box>
        </ModalContent>
      </Modal>
    </>
  );
}

export default FoodCard; 