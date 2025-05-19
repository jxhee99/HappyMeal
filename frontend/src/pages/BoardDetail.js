import React, { useState, useEffect } from 'react';
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
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  Favorite as FavoriteIcon,
  FavoriteBorder as FavoriteBorderIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  ArrowBack as ArrowBackIcon,
  ThumbUp as ThumbUpIcon,
  Send as SendIcon,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import BoardService from '../services/BoardService';

const BoardDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [liked, setLiked] = useState(false);

  useEffect(() => {
    console.log('BoardDetail 마운트, id:', id); // 디버깅용 로그
    
    if (!id) {
      console.error('id가 없습니다.'); // 디버깅용 로그
      setError('게시글 ID가 없습니다.');
      setLoading(false);
      return;
    }

    const fetchPostDetail = async () => {
      try {
        setLoading(true);
        setError('');
        console.log('게시글 상세 조회 시작:', { id }); // 디버깅용 로그
        const response = await BoardService.getBoardDetail(id);
        console.log('게시글 상세 응답:', response.data); // 디버깅용 로그
        console.log('게시글 블록 데이터:', response.data.blocks); // 블록 데이터 디버깅
        
        if (!response.data.blocks || !Array.isArray(response.data.blocks)) {
          console.warn('블록 데이터가 없거나 배열이 아닙니다:', response.data.blocks);
        }
        
        setPost(response.data);
        setComments(response.data.comments || []);
        setLiked(response.data.liked || false);
      } catch (error) {
        console.error('게시글 상세 조회 실패:', error);
        if (error.response) {
          const errorMessage = error.response.data?.message || '알 수 없는 오류가 발생했습니다.';
          setError(`게시글을 불러오는데 실패했습니다. (${error.response.status}): ${errorMessage}`);
          console.error('서버 응답:', error.response.data); // 디버깅용 로그
        } else {
          setError('게시글을 불러오는데 실패했습니다.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchPostDetail();
  }, [id]);

  const handleLike = async () => {
    if (!id) return;
    
    try {
      if (liked) {
        await BoardService.unlikeBoard(id);
      } else {
        await BoardService.likeBoard(id);
      }
      setLiked(!liked);
      setPost(prev => ({
        ...prev,
        likesCount: liked ? prev.likesCount - 1 : prev.likesCount + 1
      }));
    } catch (error) {
      console.error('좋아요 처리 실패:', error);
      setError('좋아요 처리에 실패했습니다.');
    }
  };

  const handleDelete = async () => {
    if (!id) return;
    
    if (window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
      try {
        await BoardService.deleteBoard(id);
        navigate('/board');
      } catch (error) {
        console.error('게시글 삭제 실패:', error);
        setError('게시글 삭제에 실패했습니다.');
      }
    }
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim() || !id) return;

    try {
      // TODO: 댓글 작성 API 연동
      const comment = {
        id: Date.now(),
        content: newComment,
        author: '현재 사용자',
        createdAt: new Date().toISOString(),
      };
      setComments([...comments, comment]);
      setNewComment('');
    } catch (error) {
      console.error('댓글 작성 실패:', error);
      setError('댓글 작성에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/board')}
          sx={{ mt: 2 }}
        >
          목록으로 돌아가기
        </Button>
      </Container>
    );
  }

  if (!post) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Alert severity="warning" sx={{ mb: 2 }}>
          게시글을 찾을 수 없습니다.
        </Alert>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/board')}
          sx={{ mt: 2 }}
        >
          목록으로 돌아가기
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Paper sx={{ p: 3, borderRadius: 2 }}>
        {/* 헤더 */}
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <IconButton onClick={() => navigate('/board')} sx={{ mr: 2 }}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h5" component="h1" sx={{ flexGrow: 1 }}>
            {post.title}
          </Typography>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <IconButton onClick={handleLike} color={liked ? 'primary' : 'default'}>
              <ThumbUpIcon />
            </IconButton>
            <IconButton onClick={() => navigate(`/board/edit/${id}`)}>
              <EditIcon />
            </IconButton>
            <IconButton onClick={handleDelete} color="error">
              <DeleteIcon />
            </IconButton>
          </Box>
        </Box>

        <Divider sx={{ my: 2 }} />

        {/* 게시글 정보 */}
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Avatar sx={{ mr: 2 }} src={post.userProfileImgUrl}>
            {post.nickname?.[0]}
          </Avatar>
          <Box>
            <Typography variant="subtitle1">{post.nickname}</Typography>
            <Typography variant="caption" color="text.secondary">
              {post.createdAt ? new Date(post.createdAt).toLocaleString('ko-KR', { dateStyle: 'medium', timeStyle: 'short' }) : ''}
            </Typography>
          </Box>
          <Box sx={{ ml: 'auto', display: 'flex', gap: 2 }}>
            <Typography variant="body2" color="text.secondary">
              조회수: {post.views}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              좋아요: {post.likesCount}
            </Typography>
          </Box>
        </Box>

        <Divider sx={{ my: 2 }} />

        {/* 게시글 내용 */}
        <Box sx={{ mb: 4 }}>
          {post.blocks && post.blocks.length > 0 ? (
            post.blocks.map((block, index) => {
              console.log('블록 렌더링:', block); // 블록 렌더링 디버깅
              return (
                <Box key={block.blockId || index} sx={{ mb: 2 }}>
                  {block.blockType === 'text' && (
                    <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
                      {block.contentText}
                    </Typography>
                  )}
                  {block.blockType === 'image' && (
                    <Box
                      component="img"
                      src={block.imageUrl || block.image_url}
                      alt={block.imageCaption || block.image_caption || `이미지 ${index + 1}`}
                      sx={{
                        maxWidth: '100%',
                        height: 'auto',
                        borderRadius: 1,
                        mb: 2,
                      }}
                    />
                  )}
                </Box>
              );
            })
          ) : (
            <Typography variant="body1" color="text.secondary">
              내용이 없습니다.
            </Typography>
          )}
        </Box>

        <Divider sx={{ my: 2 }} />

        {/* 댓글 섹션 */}
        <Box>
          <Typography variant="h6" sx={{ mb: 2 }}>
            댓글 {comments.length}개
          </Typography>

          {/* 댓글 작성 폼 */}
          <Box component="form" onSubmit={handleCommentSubmit} sx={{ mb: 3 }}>
            <TextField
              fullWidth
              multiline
              rows={2}
              placeholder="댓글을 작성하세요..."
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              sx={{ mb: 1 }}
            />
            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                type="submit"
                variant="contained"
                endIcon={<SendIcon />}
                disabled={!newComment.trim()}
              >
                댓글 작성
              </Button>
            </Box>
          </Box>

          {/* 댓글 목록 */}
          <List>
            {comments.map((comment) => (
              <ListItem key={comment.id} alignItems="flex-start">
                <ListItemAvatar>
                  <Avatar>{comment.author?.[0]}</Avatar>
                </ListItemAvatar>
                <ListItemText
                  primary={comment.author}
                  secondary={
                    <>
                      <Typography
                        component="span"
                        variant="body2"
                        color="text.primary"
                        sx={{ display: 'block', mb: 0.5 }}
                      >
                        {comment.content}
                      </Typography>
                      <Typography
                        component="span"
                        variant="caption"
                        color="text.secondary"
                      >
                        {new Date(comment.createdAt).toLocaleString()}
                      </Typography>
                    </>
                  }
                />
              </ListItem>
            ))}
          </List>
        </Box>
      </Paper>
    </Container>
  );
};

export default BoardDetail; 