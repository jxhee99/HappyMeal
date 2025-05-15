import React from 'react';
import { Box, Paper, Typography, LinearProgress } from '@mui/material';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';

const MotionPaper = motion(Paper);

const StyledProgress = styled(LinearProgress)(({ theme }) => ({
  height: 8,
  borderRadius: 4,
  backgroundColor: 'rgba(255, 255, 255, 0.1)',
  '& .MuiLinearProgress-bar': {
    borderRadius: 4,
  },
}));

const NutritionItem = ({ label, value, target, color }) => {
  const percentage = (value / target) * 100;
  
  return (
    <Box sx={{ mb: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
        <Typography
          variant="body2"
          sx={{
            fontFamily: 'Noto Sans KR',
            fontWeight: 500,
            color: 'text.secondary',
          }}
        >
          {label}
        </Typography>
        <Typography
          variant="body2"
          sx={{
            fontFamily: 'Noto Sans KR',
            fontWeight: 600,
            color: 'text.primary',
          }}
        >
          {value}/{target}g
        </Typography>
      </Box>
      <StyledProgress
        variant="determinate"
        value={Math.min(percentage, 100)}
        sx={{
          '& .MuiLinearProgress-bar': {
            background: `linear-gradient(90deg, ${color} 0%, ${color}99 100%)`,
          },
        }}
      />
    </Box>
  );
};

const NutritionSummary = () => {
  const nutritionData = [
    { label: '단백질', value: 75, target: 100, color: '#FF6B6B' },
    { label: '지방', value: 45, target: 60, color: '#FF8E53' },
    { label: '탄수화물', value: 200, target: 300, color: '#FFB347' },
    { label: '식이섬유', value: 15, target: 25, color: '#FFD700' },
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
        오늘의 영양소
      </Typography>
      <Box>
        {nutritionData.map((item) => (
          <NutritionItem key={item.label} {...item} />
        ))}
      </Box>
      <Box
        sx={{
          mt: 3,
          pt: 2,
          borderTop: '1px solid rgba(255, 255, 255, 0.1)',
        }}
      >
        <Typography
          variant="body2"
          sx={{
            fontFamily: 'Noto Sans KR',
            color: 'text.secondary',
            textAlign: 'center',
          }}
        >
          목표 달성률: 75%
        </Typography>
      </Box>
    </MotionPaper>
  );
};

export default NutritionSummary; 