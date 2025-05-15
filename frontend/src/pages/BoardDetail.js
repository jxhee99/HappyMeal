import React, { useState } from 'react';
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Button,
  TextField,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  IconButton,
  Divider,
} from '@mui/material';
import {
  Favorite as FavoriteIcon,
  FavoriteBorder as FavoriteBorderIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';

const BoardDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [comment, setComment] = useState('');
  const [isLiked, setIsLiked] = useState(false);

  // TODO: API 연동
  const [post] = useState({
    id: 1,
    title: '샘플 게시글 1',
    category: '맛집 추천',
    author: '사용자1',
    date: '2024-03-20',
    content: '게시글 내용입니다.',
    views: 100,
    likes: 10,
    images: [],
  });

  const [comments] = useState([
    {
      id: 1,
      author: '사용자2',
      content: '댓글 내용 1',
      date: '2024-03-20',
    },
  ]);

  const handleLike = async () => {
    // TODO: API 연동
    setIsLiked(!isLiked);
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    // TODO: API 연동
    setComment('');
  };

  const handleEdit = () => {
    navigate(`/board/edit/${id}`);
  };

  const handleDelete = async () => {
    // TODO: API 연동
    navigate('/board');
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Grid container spacing={3}>
        {/* 게시글 내용 */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h5" component="h1">
                {post.title}
              </Typography>
              <Box>
                <IconButton onClick={handleLike} color="error">
                  {isLiked ? <FavoriteIcon /> : <FavoriteBorderIcon />}
                </IconButton>
                <Button
                  variant="outlined"
                  startIcon={<EditIcon />}
                  onClick={handleEdit}
                  sx={{ mr: 1 }}
                >
                  수정
                </Button>
                <Button
                  variant="outlined"
                  color="error"
                  startIcon={<DeleteIcon />}
                  onClick={handleDelete}
                >
                  삭제
                </Button>
              </Box>
            </Box>
            <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
              <Typography variant="body2" color="text.secondary">
                작성자: {post.author}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                작성일: {post.date}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                조회수: {post.views}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                좋아요: {post.likes}
              </Typography>
            </Box>
            <Divider sx={{ mb: 3 }} />
            <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
              {post.content}
            </Typography>
            {post.images.length > 0 && (
              <Box sx={{ mt: 3 }}>
                {post.images.map((image, index) => (
                  <img
                    key={index}
                    src={image}
                    alt={`첨부 이미지 ${index + 1}`}
                    style={{ maxWidth: '100%', marginBottom: '1rem' }}
                  />
                ))}
              </Box>
            )}
          </Paper>
        </Grid>

        {/* 댓글 작성 */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              댓글 작성
            </Typography>
            <Box component="form" onSubmit={handleCommentSubmit}>
              <TextField
                fullWidth
                multiline
                rows={3}
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                placeholder="댓글을 작성하세요"
                sx={{ mb: 2 }}
              />
              <Button
                type="submit"
                variant="contained"
                color="primary"
                disabled={!comment.trim()}
              >
                댓글 작성
              </Button>
            </Box>
          </Paper>
        </Grid>

        {/* 댓글 목록 */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              댓글 {comments.length}개
            </Typography>
            <List>
              {comments.map((comment) => (
                <React.Fragment key={comment.id}>
                  <ListItem alignItems="flex-start">
                    <ListItemAvatar>
                      <Avatar>{comment.author[0]}</Avatar>
                    </ListItemAvatar>
                    <ListItemText
                      primary={
                        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                          <Typography component="span" variant="subtitle2">
                            {comment.author}
                          </Typography>
                          <Typography component="span" variant="body2" color="text.secondary">
                            {comment.date}
                          </Typography>
                        </Box>
                      }
                      secondary={comment.content}
                    />
                  </ListItem>
                  <Divider variant="inset" component="li" />
                </React.Fragment>
              ))}
            </List>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default BoardDetail; 