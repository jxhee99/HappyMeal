import React from 'react';
import { Box, Button, Typography, Container } from '@mui/material';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';
import GoogleIcon from '@mui/icons-material/Google';

const MotionBox = motion(Box);

const StyledButton = styled(Button)(({ theme }) => ({
  padding: '12px 24px',
  borderRadius: '12px',
  textTransform: 'none',
  fontFamily: 'Noto Sans KR',
  fontWeight: 600,
  fontSize: '1rem',
  boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
  '&:hover': {
    boxShadow: '0 6px 16px rgba(0, 0, 0, 0.15)',
  },
}));

const Login = () => {
  const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  return (
    <Container maxWidth="sm">
      <MotionBox
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '80vh',
          textAlign: 'center',
        }}
      >
        <Typography
          variant="h3"
          sx={{
            mb: 4,
            fontFamily: 'Noto Sans KR',
            fontWeight: 700,
            background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
          }}
        >
          해피밀에 오신 것을 환영합니다
        </Typography>
        
        <Typography
          variant="h6"
          sx={{
            mb: 6,
            fontFamily: 'Noto Sans KR',
            color: 'text.secondary',
          }}
        >
          건강한 식습관을 위한 첫 걸음을 시작해보세요
        </Typography>

        <StyledButton
          variant="contained"
          startIcon={<GoogleIcon />}
          onClick={handleGoogleLogin}
          sx={{
            background: 'white',
            color: '#757575',
            '&:hover': {
              background: '#f5f5f5',
            },
          }}
        >
          Google 계정으로 로그인
        </StyledButton>
      </MotionBox>
    </Container>
  );
};

export default Login; 