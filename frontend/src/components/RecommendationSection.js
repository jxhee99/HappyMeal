import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Button,
  Paper,
  useTheme,
  useMediaQuery
} from '@mui/material';

const pastelColors = [
  '#e3f6f5', // 건강식
  '#d6f5d6', // 다이어트
  '#d6e6fa', // 벌크업
  '#ffe5d9'  // 치팅
];

const recommendedMeals = [
  {
    category: '건강식',
    image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd',
    calories: '86g',
    protein: '5g',
    etc: '14~59g',
    tibac: '26g',
    color: pastelColors[0],
    info: [
      { label: '칼로리', value: '86g' },
      { label: '단백질', value: '5g' },
      { label: '지방', value: '14~59g' },
      { label: '티박', value: '26g' }
    ]
  },
  {
    category: '다이어트',
    image: 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2',
    calories: '36g',
    protein: '4g',
    etc: '18g',
    tibac: '13g',
    color: pastelColors[1],
    info: [
      { label: '칼로리', value: '36g' },
      { label: '단백질', value: '4g' },
      { label: '지방', value: '18g' },
      { label: '티박', value: '13g' }
    ]
  },
  {
    category: '벌크업',
    image: 'https://images.unsplash.com/photo-1467003909585-2f8a72700288',
    calories: '51g',
    protein: '15g',
    etc: '17g',
    tibac: '6g',
    color: pastelColors[2],
    info: [
      { label: '칼로리', value: '51g' },
      { label: '단백질', value: '15g' },
      { label: '지방', value: '17g' },
      { label: '티박', value: '6g' }
    ]
  },
  {
    category: '치팅',
    image: 'https://images.unsplash.com/photo-1513104890138-7c749659a591',
    calories: '31g',
    protein: '7g',
    etc: '7g',
    tibac: '5g',
    color: pastelColors[3],
    info: [
      { label: '칼로리', value: '31g' },
      { label: '단백질', value: '7g' },
      { label: '지방', value: '7g' },
      { label: '티박', value: '5g' }
    ]
  }
];

const RecommendationSection = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <Box sx={{
      width: '100%',
      py: isMobile ? 4 : 8,
      background: '#fff9f3',
      borderRadius: 4,
      textAlign: 'center',
    }}>
      <Typography
        variant={isMobile ? 'h5' : 'h4'}
        sx={{
          fontWeight: 700,
          color: '#ff5a36',
          mb: isMobile ? 3 : 5,
          letterSpacing: '-1px',
        }}
      >
        오늘의 추천 식단
      </Typography>
      <Grid container spacing={isMobile ? 2 : 4} justifyContent="center">
        {recommendedMeals.map((meal, idx) => (
          <Grid item xs={12} sm={6} md={3} key={meal.category}>
            <Paper
              elevation={0}
              sx={{
                background: meal.color,
                borderRadius: 3,
                p: isMobile ? 2 : 3,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                minHeight: 380,
                boxShadow: '0 2px 12px rgba(0,0,0,0.04)',
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'translateY(-6px) scale(1.03)',
                  boxShadow: '0 8px 24px rgba(255,90,54,0.10)',
                },
              }}
            >
              <Box
                component="img"
                src={meal.image}
                alt={meal.category}
                sx={{
                  width: isMobile ? 120 : 150,
                  height: isMobile ? 120 : 150,
                  objectFit: 'cover',
                  borderRadius: 2,
                  mb: 2,
                  boxShadow: '0 2px 8px rgba(0,0,0,0.06)'
                }}
              />
              <Typography
                variant="h6"
                sx={{
                  fontWeight: 700,
                  color: '#ff5a36',
                  mb: 1.5,
                  fontSize: isMobile ? '1.1rem' : '1.25rem',
                  letterSpacing: '-0.5px',
                }}
              >
                {meal.category}
              </Typography>
              <Box sx={{ mb: 2, width: '100%' }}>
                {meal.info.map((item) => (
                  <Box
                    key={item.label}
                    sx={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      fontSize: isMobile ? '0.95rem' : '1.05rem',
                      color: '#ff5a36',
                      fontWeight: 500,
                      mb: 0.5,
                      px: 1
                    }}
                  >
                    <span>{item.label}</span>
                    <span>{item.value}</span>
                  </Box>
                ))}
              </Box>
              <Button
                variant="outlined"
                sx={{
                  borderColor: '#ff5a36',
                  color: '#ff5a36',
                  fontWeight: 700,
                  borderRadius: 2,
                  px: 4,
                  py: 1,
                  mt: 'auto',
                  '&:hover': {
                    background: '#ff5a36',
                    color: '#fff',
                    borderColor: '#ff5a36',
                  },
                }}
              >
                추가
              </Button>
            </Paper>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default RecommendationSection; 