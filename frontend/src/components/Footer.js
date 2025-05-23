import React from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import InstagramIcon from "@mui/icons-material/Instagram";
import TwitterIcon from "@mui/icons-material/Twitter";
import FacebookIcon from "@mui/icons-material/Facebook";
import { motion } from 'framer-motion';

const MotionBox = motion(Box);

function Footer() {
  return (
    <Box className="footer">
      <Box sx={{ position: 'relative', zIndex: 1 }}>
        <MotionBox
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
        >
          <Typography
            variant="h5"
            sx={{
              fontWeight: 800,
              mb: 2,
              letterSpacing: 1,
              textShadow: '0 2px 4px rgba(0,0,0,0.1)',
            }}
          >
            HappyMeal
          </Typography>
        </MotionBox>

        <MotionBox
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.2 }}
        >
          <Typography
            sx={{
              mb: 3,
              fontSize: 16,
              lineHeight: 1.6,
              maxWidth: '600px',
              mx: 'auto',
            }}
          >
            fresh everyday!<br />
            HappyMeal is a fruit & vegetable shop
          </Typography>
        </MotionBox>

        <MotionBox
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.4 }}
        >
          <Box sx={{ mb: 3 }}>
            <IconButton
              href="#"
              sx={{
                color: '#fff',
                background: 'rgba(255, 255, 255, 0.1)',
                backdropFilter: 'blur(10px)',
                mx: 1,
                '&:hover': {
                  background: 'rgba(255, 255, 255, 0.2)',
                  transform: 'translateY(-2px)',
                },
                transition: 'all 0.3s ease',
              }}
            >
              <InstagramIcon />
            </IconButton>
            <IconButton
              href="#"
              sx={{
                color: '#fff',
                background: 'rgba(255, 255, 255, 0.1)',
                backdropFilter: 'blur(10px)',
                mx: 1,
                '&:hover': {
                  background: 'rgba(255, 255, 255, 0.2)',
                  transform: 'translateY(-2px)',
                },
                transition: 'all 0.3s ease',
              }}
            >
              <TwitterIcon />
            </IconButton>
            <IconButton
              href="#"
              sx={{
                color: '#fff',
                background: 'rgba(255, 255, 255, 0.1)',
                backdropFilter: 'blur(10px)',
                mx: 1,
                '&:hover': {
                  background: 'rgba(255, 255, 255, 0.2)',
                  transform: 'translateY(-2px)',
                },
                transition: 'all 0.3s ease',
              }}
            >
              <FacebookIcon />
            </IconButton>
          </Box>
        </MotionBox>

        <MotionBox
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.6 }}
        >
          <Typography
            sx={{
              fontSize: 13,
              color: 'rgba(255, 255, 255, 0.8)',
              lineHeight: 1.8,
            }}
          >
            Hosting by imweb<br />
            이용약관 · 개인정보처리방침<br />
            Copyright © 2025 HappyMeal All rights reserved.
          </Typography>
        </MotionBox>
      </Box>
    </Box>
  );
}

export default Footer; 