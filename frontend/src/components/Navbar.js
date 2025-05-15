import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Button, IconButton, Drawer, List, ListItem, ListItemText, Box, useTheme, useMediaQuery } from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { motion } from 'framer-motion';
import { useAuth } from '../contexts/AuthContext';

const MotionBox = motion(Box);

const Navbar = () => {
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getMenuItems = () => {
    const baseItems = [
      { text: '홈', path: '/' },
      { text: '음식', path: '/foods' },
      { text: '식단 기록', path: '/meallogs' },
      { text: '게시판', path: '/board' },
      { text: '마이페이지', path: '/mypage' },

    ];

    if (isAuthenticated) {
      return [
        ...baseItems,
        { 
          text: '로그아웃', 
          path: '/logout',
          onClick: handleLogout
        }
      ];
    }

    return [
      ...baseItems,
      { text: '로그인', path: '/login' }
    ];
  };

  const menuItems = getMenuItems();

  const drawer = (
    <List>
      {menuItems.map((item) => (
        <ListItem
          button
          component={item.path ? Link : 'div'}
          to={item.path}
          key={item.text}
          onClick={item.onClick || handleDrawerToggle}
          sx={{
            background: location.pathname === item.path ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
            borderRadius: '12px',
            margin: '4px 8px',
            '&:hover': {
              background: 'rgba(255, 255, 255, 0.1)',
            }
          }}
        >
          <ListItemText
            primary={item.text}
            sx={{
              color: '#fff',
              textAlign: 'center',
              '& .MuiTypography-root': {
                fontWeight: location.pathname === item.path ? 700 : 500,
              },
            }}
          />
        </ListItem>
      ))}
    </List>
  );

  return (
    <AppBar position="sticky" className="navbar" elevation={0}>
      <Toolbar>
        <MotionBox
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Typography
            variant="h6"
            component={Link}
            to="/"
            className="navbar-logo"
            sx={{ textDecoration: 'none' }}
          >
            fruits basket
          </Typography>
        </MotionBox>

        {isMobile ? (
          <>
            <IconButton
              color="inherit"
              aria-label="open drawer"
              edge="end"
              onClick={handleDrawerToggle}
              sx={{ ml: 'auto' }}
            >
              <MenuIcon />
            </IconButton>
            <Drawer
              anchor="right"
              open={mobileOpen}
              onClose={handleDrawerToggle}
              PaperProps={{
                sx: {
                  background: 'linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%)',
                  width: '70%',
                  maxWidth: '300px',
                },
              }}
            >
              {drawer}
            </Drawer>
          </>
        ) : (
          <Box className="navbar-menu" sx={{ display: 'flex', alignItems: 'center', ml: 'auto' }}>
            {menuItems.map((item) => (
              <MotionBox
                key={item.text}
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: menuItems.indexOf(item) * 0.1 }}
              >
                <Button
                  component={Link}
                  to={item.path}
                  onClick={item.onClick}
                  sx={{
                    color: '#fff',
                    fontWeight: location.pathname === item.path ? 700 : 500,
                    background: location.pathname === item.path ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
                    '&:hover': {
                      background: 'rgba(255, 255, 255, 0.15)',
                    }
                  }}
                >
                  {item.text}
                </Button>
              </MotionBox>
            ))}
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Navbar; 