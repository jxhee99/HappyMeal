import React, { useState } from 'react';
import { Box, Paper, Typography, IconButton } from '@mui/material';
import { DateCalendar } from '@mui/x-date-pickers/DateCalendar';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { ko } from 'date-fns/locale';
import { format } from 'date-fns';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

const MotionPaper = motion(Paper);

const StyledCalendar = styled(Box)(({ theme }) => ({
  '& .MuiPickersCalendarHeader-root': {
    margin: '0 8px',
  },
  '& .MuiPickersCalendarHeader-label': {
    fontSize: '1.1rem',
    fontWeight: 600,
    color: theme.palette.text.primary,
  },
  '& .MuiDayCalendar-weekDayLabel': {
    fontSize: '0.875rem',
    fontWeight: 500,
    color: theme.palette.text.secondary,
  },
  '& .MuiPickersDay-root': {
    fontSize: '0.875rem',
    '&.Mui-selected': {
      backgroundColor: '#FF6B6B',
      color: 'white',
      '&:hover': {
        backgroundColor: '#FF8E53',
      },
    },
    '&.MuiPickersDay-today': {
      border: '2px solid #FF6B6B',
    },
  },
}));

const MealLogCalendar = ({ onDateSelect }) => {
  const [selectedDate, setSelectedDate] = useState(new Date());

  const handleDateChange = (newDate) => {
    setSelectedDate(newDate);
    const formattedDate = format(newDate, 'yyyy-MM-dd');
    onDateSelect(formattedDate);
  };

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
        식단 기록
      </Typography>
      <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ko}>
        <StyledCalendar>
          <DateCalendar
            value={selectedDate}
            onChange={handleDateChange}
            maxDate={new Date()}
            sx={{
              width: '100%',
              '& .MuiPickersCalendarHeader-root': {
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
              },
            }}
            slots={{
              previousIconButton: (props) => (
                <IconButton {...props}>
                  <ArrowBackIosNewIcon />
                </IconButton>
              ),
              nextIconButton: (props) => (
                <IconButton {...props}>
                  <ArrowForwardIosIcon />
                </IconButton>
              ),
            }}
          />
        </StyledCalendar>
      </LocalizationProvider>
    </MotionPaper>
  );
};

export default MealLogCalendar; 