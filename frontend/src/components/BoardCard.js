import React from 'react';
import {
  Card,
  CardContent,
  CardMedia,
  Typography,
  Box,
  Chip,
  IconButton,
  Tooltip,
  Divider,
} from '@mui/material';
import {
  Favorite as FavoriteIcon,
  FavoriteBorder as FavoriteBorderIcon,
  Visibility as VisibilityIcon,
  AccessTime as AccessTimeIcon,
  ChatBubbleOutline as ChatBubbleOutlineIcon,
} from '@mui/icons-material';

const BoardCard = ({ post, categories, onClick }) => {
  // 데이터 매핑 및 디버깅
  const mappedPost = {
    ...post,
    content: post.content || '',
    imageUrl: post.imageUrl || '',
    nickName: post.nickName || '알 수 없음',
    views: post.views || 0,
    likesCount: post.likesCount || 0,
    commentsCount: post.commentsCount || 0
  };

  console.log('원본 post 데이터:', post);
  console.log('매핑된 post 데이터:', mappedPost);
  console.log('content 필드:', post.content);
  console.log('imageUrl 필드:', post.imageUrl);

  return (
    <Card 
      sx={{ 
        height: '100%', 
        display: 'flex',
        cursor: 'pointer',
        minHeight: 200,
        '&:hover': {
          boxShadow: 6,
        },
      }}
      onClick={onClick}
    >
      <CardMedia
        component="img"
        sx={{ 
          width: 300,
          flexShrink: 0,
          objectFit: 'cover',
        }}
        image={mappedPost.imageUrl || "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&auto=format&fit=crop&q=60"}
        alt={mappedPost.title}
      />
      <CardContent sx={{ 
        flexGrow: 1,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        p: 3,
      }}>
        <Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
            <Chip
              label={categories.find(c => c.id === Number(mappedPost.categoryId))?.name || '기타'}
              size="small"
              color="primary"
              variant="outlined"
            />
            <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <AccessTimeIcon fontSize="small" />
              {new Date(mappedPost.createAt).toLocaleDateString()}
            </Typography>
          </Box>
          <Typography 
            gutterBottom 
            variant="h5" 
            component="div" 
            sx={{ 
              mb: 2,
              fontWeight: 'bold',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
            }}
          >
            {mappedPost.title || '제목 없음'}
          </Typography>
          <Typography 
            variant="body1" 
            color="text.secondary" 
            sx={{ 
              mb: 2,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
            }}
          >
            {mappedPost.content || '내용 없음'}
          </Typography>
        </Box>
        <Box>
          <Divider sx={{ my: 2 }} />
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              {mappedPost.nickName}
            </Typography>
            <Box sx={{ display: 'flex', gap: 3 }}>
              <Tooltip title="조회수">
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <VisibilityIcon fontSize="small" />
                  <Typography variant="body2">{mappedPost.views}</Typography>
                </Box>
              </Tooltip>
              <Tooltip title="좋아요">
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <FavoriteIcon fontSize="small" />
                  <Typography variant="body2">{mappedPost.likesCount}</Typography>
                </Box>
              </Tooltip>
              <Tooltip title="댓글">
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <ChatBubbleOutlineIcon fontSize="small" />
                  <Typography variant="body2">{mappedPost.commentsCount}</Typography>
                </Box>
              </Tooltip>
            </Box>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

export default BoardCard; 