import React, { useState, useEffect } from 'react';
import { Container, Grid, Typography, Box, Button, CircularProgress, Tab, Tabs, useMediaQuery, useTheme } from '@mui/material';
import { motion } from 'framer-motion';
import { styled } from '@mui/material/styles';
import { foodService } from '../services/foodService';
import BoardService from '../services/BoardService';
import FoodCard from '../components/FoodCard';
import { useNavigate } from 'react-router-dom';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import PeopleIcon from '@mui/icons-material/People';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';

const MotionBox = motion(Box);

const HeroSection = styled(Box)(({ theme }) => ({
  textAlign: 'center',
  padding: theme.spacing(12, 3),
  background: 'linear-gradient(135deg, rgba(255, 107, 107, 0.1), rgba(255, 142, 83, 0.1))',
  borderRadius: '32px',
  marginBottom: theme.spacing(8),
  position: 'relative',
  overflow: 'hidden',
  '&::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'url("https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&q=80&w=2000") center/cover',
    opacity: 0.1,
    zIndex: 0,
  },
}));

const StatsSection = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'space-around',
  flexWrap: 'wrap',
  gap: theme.spacing(4),
  marginBottom: theme.spacing(8),
  padding: theme.spacing(4),
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  borderRadius: '24px',
  backdropFilter: 'blur(10px)',
}));

const StatItem = styled(Box)(({ theme }) => ({
  textAlign: 'center',
  padding: theme.spacing(3),
  minWidth: '200px',
}));

const CategorySection = styled(Box)(({ theme }) => ({
  marginBottom: theme.spacing(8),
  padding: theme.spacing(4),
  borderRadius: '24px',
  background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
  backdropFilter: 'blur(10px)',
}));

const ArticleSection = styled(Box)(({ theme }) => ({
  marginBottom: theme.spacing(8),
  padding: theme.spacing(6),
  background: '#f8f9fa',
  borderRadius: '24px',
}));

const ArticleGrid = styled(Box)(({ theme }) => ({
  display: 'grid',
  gridTemplateColumns: 'repeat(4, 1fr)',
  gridTemplateRows: 'repeat(2, 260px)',
  gap: theme.spacing(3),
  [theme.breakpoints.down('md')]: {
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'repeat(6, 200px)',
  },
}));

const ArticleCardBig = styled(Box)(({ theme }) => ({
  position: 'relative',
  borderRadius: '16px',
  overflow: 'hidden',
  cursor: 'pointer',
  gridRow: '1 / 2',
  height: '100%',
  background: '#eee',
  boxShadow: '0 4px 16px rgba(0,0,0,0.08)',
  '&:hover .overlay': {
    opacity: 1,
  },
}));

const ArticleCardSmall = styled(Box)(({ theme }) => ({
  position: 'relative',
  borderRadius: '16px',
  overflow: 'hidden',
  cursor: 'pointer',
  gridRow: '2 / 3',
  height: '100%',
  background: '#eee',
  boxShadow: '0 4px 16px rgba(0,0,0,0.08)',
  '&:hover .overlay': {
    opacity: 1,
  },
}));

const ArticleImageStyled = styled('img')({
  width: '100%',
  height: '100%',
  objectFit: 'cover',
  display: 'block',
});

const ArticleOverlay = styled(Box)(({ theme }) => ({
  position: 'absolute',
  left: 0,
  right: 0,
  bottom: 0,
  top: 0,
  background: 'linear-gradient(0deg, rgba(0,0,0,0.6) 40%, rgba(0,0,0,0.1) 100%)',
  color: 'white',
  opacity: 0.85,
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'flex-end',
  padding: theme.spacing(3),
  transition: 'opacity 0.2s',
  zIndex: 1,
}));

const StyledTabs = styled(Tabs)(({ theme }) => ({
  marginBottom: theme.spacing(4),
  '& .MuiTabs-indicator': {
    backgroundColor: '#FF6B6B',
    height: 3,
  },
}));

const StyledTab = styled(Tab)(({ theme }) => ({
  textTransform: 'none',
  fontWeight: 600,
  fontSize: '1.1rem',
  minWidth: 100,
  '&.Mui-selected': {
    color: '#FF6B6B',
  },
}));

const Home = () => {
  const [categoryFoods, setCategoryFoods] = useState({
    diet: [],
    healthy: [],
    'bulk-up': [],
    cheating: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedTab, setSelectedTab] = useState(0);
  const [articles, setArticles] = useState([]);
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const categoryInfo = {
    diet: {
      title: '다이어트 식단',
      description: '저칼로리, 고단백 식단으로 건강한 다이어트를 도와드립니다',
      gradient: 'linear-gradient(135deg, #4CAF50, #8BC34A)'
    },
    healthy: {
      title: '건강한 식단',
      description: '균형 잡힌 영양소로 건강한 식습관을 만들어보세요',
      gradient: 'linear-gradient(135deg, #2196F3, #03A9F4)'
    },
    'bulk-up': {
      title: '근육 증량 식단',
      description: '고칼로리, 고단백 식단으로 근육을 키워보세요',
      gradient: 'linear-gradient(135deg, #FF9800, #FFC107)'
    },
    cheating: {
      title: '치팅데이 식단',
      description: '가끔은 맛있는 음식으로 기분 전환을 해보세요',
      gradient: 'linear-gradient(135deg, #E91E63, #FF4081)'
    }
  };

  const stats = [
    { icon: <RestaurantIcon sx={{ fontSize: 40 }} />, value: '10,000+', label: '푸드 데이터' },
    { icon: <PeopleIcon sx={{ fontSize: 40 }} />, value: '1,000,000+', label: '이용자 수' },
    { icon: <TrendingUpIcon sx={{ fontSize: 40 }} />, value: '500,000+', label: '하루 접속량' },
  ];

  useEffect(() => {
    const fetchAllCategoryFoods = async () => {
      try {
        setLoading(true);
        const categories = Object.keys(categoryFoods);
        const results = await Promise.all(
          categories.map(category => foodService.getRecommendedFoods(category))
        );
        
        const newCategoryFoods = {};
        categories.forEach((category, index) => {
          newCategoryFoods[category] = results[index] || [];
        });
        
        setCategoryFoods(newCategoryFoods);
      } catch (err) {
        setError('추천 음식을 불러오는데 실패했습니다.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchAllCategoryFoods();
  }, []);

  useEffect(() => {
    const fetchArticles = async () => {
      try {
        const response = await BoardService.getBoards({ page: 0, size: 6, sortBy: 'latest' });
        if (response.data && Array.isArray(response.data.content)) {
          setArticles(response.data.content);
        }
      } catch (err) {
        console.error('게시글 조회 실패:', err);
      }
    };

    fetchArticles();
  }, []);

  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, display: 'flex', justifyContent: 'center' }}>
        <CircularProgress />
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ py: 8, textAlign: 'center' }}>
        <Typography color="error">{error}</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 8 }}>
      <MotionBox
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <HeroSection>
          <Typography
            variant={isMobile ? "h4" : "h2"}
            sx={{
              fontFamily: 'Noto Sans KR',
              fontWeight: 800,
              mb: 3,
              background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              position: 'relative',
              zIndex: 1,
            }}
          >
            건강한 식습관의 시작
          </Typography>
          <Typography
            variant={isMobile ? "subtitle1" : "h6"}
            sx={{
              fontFamily: 'Noto Sans KR',
              color: 'text.secondary',
              mb: 4,
              position: 'relative',
              zIndex: 1,
            }}
          >
            균형 잡힌 영양소와 맛있는 요리로 건강한 식습관을 만들어보세요
          </Typography>
          <Button
            variant="contained"
            size="large"
            sx={{
              background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
              color: 'white',
              padding: '12px 32px',
              borderRadius: '12px',
              fontFamily: 'Noto Sans KR',
              fontWeight: 600,
              position: 'relative',
              zIndex: 1,
              '&:hover': {
                background: 'linear-gradient(135deg, #FF8E53, #FF6B6B)',
              },
            }}
            onClick={() => navigate('/meallogs')}
          >
            식단 기록하기
          </Button>
        </HeroSection>

        <StatsSection>
          {stats.map((stat, index) => (
            <StatItem key={index}>
              <Box sx={{ color: '#FF6B6B', mb: 1 }}>{stat.icon}</Box>
              <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
                {stat.value}
              </Typography>
              <Typography variant="subtitle1" color="text.secondary">
                {stat.label}
              </Typography>
            </StatItem>
          ))}
        </StatsSection>

        <StyledTabs
          value={selectedTab}
          onChange={handleTabChange}
          variant={isMobile ? "scrollable" : "fullWidth"}
          scrollButtons={isMobile ? "auto" : false}
          centered={!isMobile}
        >
          {Object.entries(categoryInfo).map(([key, info], index) => (
            <StyledTab key={key} label={info.title} />
          ))}
        </StyledTabs>

        {Object.entries(categoryFoods).map(([category, foods], index) => (
          selectedTab === index && (
            <CategorySection key={category}>
              <Typography
                variant="h4"
                sx={{
                  fontFamily: 'Noto Sans KR',
                  fontWeight: 700,
                  mb: 2,
                  background: categoryInfo[category].gradient,
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                }}
              >
                {categoryInfo[category].title}
              </Typography>
              <Typography
                variant="subtitle1"
                sx={{
                  fontFamily: 'Noto Sans KR',
                  color: 'text.secondary',
                  mb: 4,
                }}
              >
                {categoryInfo[category].description}
              </Typography>
              <Grid container spacing={4}>
                {foods.map((food) => (
                  <Grid item xs={12} sm={6} md={3} key={food.foodId}>
                    <FoodCard 
                      food={food} 
                      onClick={() => {
                        console.log('Food clicked:', food);
                      }}
                    />
                  </Grid>
                ))}
              </Grid>
            </CategorySection>
          )
        ))}

        <ArticleSection>
          <Box sx={{
            display: 'flex',
            flexDirection: { xs: 'column', md: 'row' },
            alignItems: 'center',
            justifyContent: 'space-between',
            mb: 6,
            gap: 2,
          }}>
            <Typography
              variant="h4"
              sx={{
                fontFamily: 'Noto Sans KR',
                fontWeight: 700,
                textAlign: { xs: 'center', md: 'left' },
                color: '#333',
                flex: 1,
              }}
            >
              Health Challenge Community
            </Typography>
            <Typography
              variant="subtitle1"
              sx={{
                fontFamily: 'Noto Sans KR',
                color: 'text.secondary',
                textAlign: { xs: 'center', md: 'right' },
                flex: 2,
              }}
            >
              수많은 사용자들이 직접 경험한 식단, 레시피, 건강관리 노하우와 정보를 자유롭게 공유합니다.
              <br></br>건강한 삶을 위한 다양한 팁과 챌린지, 그리고 서로의 이야기를 통해 함께 성장하는 공간입니다.
            </Typography>
          </Box>
          <ArticleGrid>
            {articles.slice(0, 6).map((article, idx) => {
              let gridColumn, gridRow, CardComp;
              if (idx === 0) {
                gridColumn = '1 / 3'; gridRow = '1 / 2'; CardComp = ArticleCardBig;
              } else if (idx === 1) {
                gridColumn = '3 / 4'; gridRow = '1 / 2'; CardComp = ArticleCardSmall;
              } else if (idx === 2) {
                gridColumn = '1 / 2'; gridRow = '2 / 3'; CardComp = ArticleCardSmall;
              } else if (idx === 3) {
                gridColumn = '2 / 4'; gridRow = '2 / 3'; CardComp = ArticleCardBig;
              } else if (idx === 4) {
                gridColumn = '4 / 5'; gridRow = '1 / 2'; CardComp = ArticleCardSmall;
              } else {
                gridColumn = '4 / 5'; gridRow = '2 / 3'; CardComp = ArticleCardSmall;
              }
              return (
                <CardComp
                  key={article.boardId}
                  style={{ gridColumn, gridRow }}
                  onClick={() => navigate(`/board/${article.boardId}`)}
                >
                  <ArticleImageStyled
                    src={article.imageUrl || 'https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&q=80&w=2000'}
                    alt={article.title}
                  />
                  <ArticleOverlay className="overlay">
                    <Typography variant={CardComp === ArticleCardBig ? 'h6' : 'subtitle1'} sx={{ fontWeight: 700, mb: 1, color: 'white', textShadow: '0 2px 8px rgba(0,0,0,0.4)' }}>
                      {article.title}
                    </Typography>
                    <Typography variant="body2" sx={{ color: 'white', textShadow: '0 1px 4px rgba(0,0,0,0.3)' }}>
                      {article.content?.slice(0, 30)}{article.content?.length > 30 ? '...' : ''}
                    </Typography>
                  </ArticleOverlay>
                </CardComp>
              );
            })}
          </ArticleGrid>
          <Box sx={{ mt: 4, textAlign: 'center' }}>
            <Button
              variant="outlined"
              onClick={() => navigate('/Board')}
              sx={{
                borderColor: '#FF6B6B',
                color: '#FF6B6B',
                '&:hover': {
                  borderColor: '#FF8E53',
                  backgroundColor: 'rgba(255, 107, 107, 0.04)',
                },
              }}
            >
              더 많은 게시글 보기
            </Button>
          </Box>
        </ArticleSection>
      </MotionBox>
    </Container>
  );
};

export default Home; 