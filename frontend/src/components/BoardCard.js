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
  ThumbUp as ThumbUpIcon,
  Visibility as VisibilityIcon,
  AccessTime as AccessTimeIcon,
} from '@mui/icons-material';

const BoardCard = ({ post, categories, onClick }) => {
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
        image="https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&auto=format&fit=crop&q=60"
        alt={post.title}
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
              label={categories.find(c => c.id === Number(post.categoryId))?.name || '기타'}
              size="small"
              color="primary"
              variant="outlined"
            />
            <Typography variant="body2" color="text.secondary" sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <AccessTimeIcon fontSize="small" />
              {new Date(post.createAt).toLocaleDateString()}
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
            {post.title || '제목 없음'}
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
            {post.content || '내용 없음'}
          </Typography>
        </Box>
        <Box>
          <Divider sx={{ my: 2 }} />
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              {post.nickName || '알 수 없음'}
            </Typography>
            <Box sx={{ display: 'flex', gap: 3 }}>
              <Tooltip title="조회수">
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <VisibilityIcon fontSize="small" />
                  <Typography variant="body2">{post.views || 0}</Typography>
                </Box>
              </Tooltip>
              <Tooltip title="좋아요">
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <ThumbUpIcon fontSize="small" />
                  <Typography variant="body2">{post.likesCount || 0}</Typography>
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