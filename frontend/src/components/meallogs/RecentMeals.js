import React from 'react';
import {
  Box,
  Typography,
  List,
  ListItem,
  ListItemText,
  Divider,
  Chip,
} from '@mui/material';
import { format, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';

const RecentMeals = ({ selectedDate, mealLogs }) => {
  if (!mealLogs || mealLogs.length === 0) {
    return (
      <Box p={3} textAlign="center">
        <Typography>선택한 날짜에 기록된 식단이 없습니다.</Typography>
      </Box>
    );
  }

  const formatDate = (dateString) => {
    try {
      return format(parseISO(dateString), 'yyyy년 MM월 dd일', { locale: ko });
    } catch (error) {
      console.error('날짜 형식 변환 오류:', error);
      return '날짜 정보 없음';
    }
  };

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        {formatDate(selectedDate)} 식단
      </Typography>
      <List>
        {mealLogs.map((log, index) => (
          <React.Fragment key={log.id || index}>
            <ListItem
              sx={{
                flexDirection: 'column',
                alignItems: 'flex-start',
                py: 2
              }}
            >
              <Box display="flex" alignItems="center" width="100%" mb={1}>
                <Chip
                  label={log.mealType}
                  size="small"
                  color="primary"
                />
              </Box>
              <ListItemText
                primary={
                  <Typography variant="body1">
                    {log.foodName}
                  </Typography>
                }
                secondary={
                  <Box mt={1}>
                    <Typography variant="body2" color="text.secondary">
                      칼로리: {log.calories}kcal | 단백질: {log.protein}g | 
                      탄수화물: {log.carbs}g | 지방: {log.fat}g
                    </Typography>
                  </Box>
                }
              />
            </ListItem>
            {index < mealLogs.length - 1 && <Divider />}
          </React.Fragment>
        ))}
      </List>
    </Box>
  );
};

export default RecentMeals; 