import React, { useState, useEffect } from 'react';
import { Box, Typography, CircularProgress, List, ListItem, ListItemText, ListItemAvatar, Avatar } from '@mui/material';
import { mealLogService } from '../../services/mealLogService';

const RecentMeals = () => {
  const [meals, setMeals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchRecentMeals = async () => {
      try {
        setLoading(true);
        const data = await mealLogService.getRecentMealLogs();
        setMeals(data);
      } catch (err) {
        setError('최근 식단 기록을 불러오는데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchRecentMeals();
  }, []);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box p={3}>
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  if (!meals.length) {
    return (
      <Box p={3}>
        <Typography>최근 식단 기록이 없습니다.</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        최근 식단 기록
      </Typography>
      <List>
        {meals.map((meal) => (
          <ListItem
            key={meal.logId}
            sx={{
              mb: 2,
              bgcolor: '#f5f5f5',
              borderRadius: 2,
              '&:hover': {
                bgcolor: '#eeeeee',
              },
            }}
          >
            <ListItemAvatar>
              <Avatar
                src={meal.imgUrl}
                alt={meal.foodName}
                sx={{ width: 56, height: 56 }}
              />
            </ListItemAvatar>
            <ListItemText
              primary={meal.foodName}
              secondary={
                <>
                  <Typography component="span" variant="body2" color="text.primary">
                    {meal.mealType} - {meal.quantity}인분
                  </Typography>
                  <br />
                  <Typography component="span" variant="body2" color="text.secondary">
                    {meal.calories} kcal | 단백질 {meal.protein}g | 탄수화물 {meal.carbs}g | 지방 {meal.fat}g
                  </Typography>
                </>
              }
            />
          </ListItem>
        ))}
      </List>
    </Box>
  );
};

export default RecentMeals; 