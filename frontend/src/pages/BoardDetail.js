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
import { useAuth } from '../contexts/AuthContext';

const BoardDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [liked, setLiked] = useState(false);
  const [replyTo, setReplyTo] = useState(null);
  const [replyContent, setReplyContent] = useState('');

  useEffect(() => {
    console.log('BoardDetail 마운트, id:', id);
    console.log('현재 로그인한 사용자:', user);
    
    if (!id) {
      console.error('id가 없습니다.');
      setError('게시글 ID가 없습니다.');
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      try {
        setLoading(true);
        setError('');
        
        // 게시글 상세 정보와 좋아요 상태만 가져옵니다
        const [boardResponse, likeResponse] = await Promise.all([
          BoardService.getBoardDetail(id),
          user ? BoardService.getLikeStatus(id) : Promise.resolve({ data: { isLiked: false } })
        ]);

        console.log('게시글 상세 응답:', boardResponse.data);
        console.log('좋아요 상태 응답:', likeResponse.data);
        
        setPost(boardResponse.data);
        setLiked(likeResponse.data.liked);
      } catch (error) {
        console.error('데이터 로딩 실패:', error);
        if (error.response) {
          const errorMessage = error.response.data?.message || '알 수 없는 오류가 발생했습니다.';
          setError(`데이터를 불러오는데 실패했습니다. (${error.response.status}): ${errorMessage}`);
          console.error('서버 응답:', error.response.data);
        } else {
          setError('데이터를 불러오는데 실패했습니다.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, user]);

  const handleLike = async () => {
    if (!id || !user) {
      setError('로그인이 필요한 기능입니다.');
      return;
    }
    
    try {
      console.log('좋아요 토글 전 상태:', { liked }); // 디버깅용 로그
      
      const response = await BoardService.toggleLike(id);
      console.log('좋아요 토글 응답:', response.data); // 디버깅용 로그
      
      // 서버 응답에 따라 상태 업데이트
      console.log('설정할 좋아요 상태:', response.data.liked);
      setLiked(response.data.liked);
      
      // 좋아요 수 업데이트
      const likesCountResponse = await BoardService.getLikesCount(id);
      setPost(prev => ({
        ...prev,
        likesCount: likesCountResponse.data
      }));
    } catch (error) {
      console.error('좋아요 처리 실패:', error);
      if (error.response?.status === 401) {
        setError('로그인이 필요한 기능입니다.');
      } else {
        setError('좋아요 처리에 실패했습니다.');
      }
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

  const handleEdit = () => {
    console.log('수정 버튼 클릭:', { id, user }); // 디버깅 로그 추가
    if (!user) {
      setError('로그인이 필요한 기능입니다.');
      return;
    }
    if (!id) {
      setError('게시글 ID가 없습니다.');
      return;
    }
    try {
      navigate(`/board/edit/${id}`, { replace: true });
    } catch (error) {
      console.error('페이지 이동 실패:', error);
      setError('페이지 이동에 실패했습니다.');
    }
  };

  // 댓글 작성 후 목록 새로고침 함수
  const refreshComments = async () => {
    try {
      const response = await BoardService.getBoardComments(id);
      setComments(response.data);
    } catch (error) {
      console.error('댓글 목록 새로고침 실패:', error);
      setError('댓글 목록을 새로고침하는데 실패했습니다.');
    }
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim() || !id) return;

    try {
      await BoardService.createComment(id, {
        content: newComment,
        parentCommentId: null
      });
      
      setNewComment('');
      await refreshComments(); // 댓글 목록 새로고침
    } catch (error) {
      console.error('댓글 작성 실패:', error);
      setError('댓글 작성에 실패했습니다.');
    }
  };

  // 대댓글 작성 취소
  const handleCancelReply = () => {
    setReplyTo(null);
    setReplyContent('');
  };

  // 대댓글 작성
  const handleReplySubmit = async (e, parentCommentId) => {
    e.preventDefault();
    if (!replyContent.trim() || !id) return;

    try {
      await BoardService.createComment(id, {
        content: replyContent,
        parentCommentId: parentCommentId
      });
      
      setReplyContent('');
      setReplyTo(null);
      await refreshComments(); // 댓글 목록 새로고침
    } catch (error) {
      console.error('대댓글 작성 실패:', error);
      setError('대댓글 작성에 실패했습니다.');
    }
  };

  // 댓글 삭제 핸들러 추가
  const handleDeleteComment = async (commentId) => {
    if (!id || !commentId) return;
    
    if (window.confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
      try {
        await BoardService.deleteComment(id, commentId);
        await refreshComments(); // 댓글 목록 새로고침
      } catch (error) {
        console.error('댓글 삭제 실패:', error);
        setError('댓글 삭제에 실패했습니다.');
      }
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
            <IconButton 
              onClick={handleLike} 
              sx={{ 
                color: liked ? '#fb3d62' : 'inherit',
                '&:hover': { color: '#fb3d62' } 
              }}
            >
              {liked ? <FavoriteIcon /> : <FavoriteBorderIcon />}
            </IconButton>
            {user && Number(user.userId) === post.userId && (
              <>
                <IconButton onClick={handleEdit}>
                  <EditIcon />
                </IconButton>
                <IconButton onClick={handleDelete} color="error">
                  <DeleteIcon />
                </IconButton>
              </>
            )}
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
            <Typography variant="body2" color="text.secondary">
              댓글: {post.commentsCount}
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
            댓글 {comments.reduce((total, comment) => total + 1 + (comment.replies?.length || 0), 0)}개
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
            {console.log('렌더링할 댓글 목록:', comments)} {/* 디버깅용 로그 추가 */}
            {comments && comments.length > 0 ? (
              comments.map((comment) => {
                console.log('현재 렌더링 중인 댓글:', comment); // 디버깅용 로그 추가
                return (
                  <React.Fragment key={comment.commentId}>
                    <ListItem alignItems="flex-start" sx={{ flexDirection: 'column' }}>
                      <Box sx={{ width: '100%', display: 'flex', alignItems: 'flex-start' }}>
                        <ListItemAvatar>
                          <Avatar>{comment.nickname?.[0]}</Avatar>
                        </ListItemAvatar>
                        <ListItemText
                          primary={
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                              <Typography variant="subtitle2">{comment.nickname}</Typography>
                              <Typography variant="caption" color="text.secondary">
                                {new Date(comment.createAt).toLocaleString('ko-KR')}
                              </Typography>
                            </Box>
                          }
                          secondary={
                            <Typography
                              component="span"
                              variant="body2"
                              color="text.primary"
                              sx={{ display: 'block', mt: 1 }}
                            >
                              {comment.content}
                            </Typography>
                          }
                        />
                        <Box sx={{ display: 'flex', gap: 1 }}>
                          <Button
                            size="small"
                            onClick={() => setReplyTo(comment.commentId)}
                          >
                            답글
                          </Button>
                          {user && Number(user.userId) === comment.userId && (
                            <Button
                              size="small"
                              color="error"
                              onClick={() => handleDeleteComment(comment.commentId)}
                            >
                              삭제
                            </Button>
                          )}
                        </Box>
                      </Box>
                      
                      {/* 대댓글 작성 폼 */}
                      {replyTo === comment.commentId && (
                        <Box component="form" onSubmit={(e) => handleReplySubmit(e, comment.commentId)} sx={{ width: '100%', mt: 2, pl: 7 }}>
                          <TextField
                            fullWidth
                            multiline
                            rows={2}
                            placeholder="대댓글을 작성하세요..."
                            value={replyContent}
                            onChange={(e) => setReplyContent(e.target.value)}
                            sx={{ mb: 1 }}
                          />
                          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                            <Button
                              size="small"
                              onClick={handleCancelReply}
                            >
                              취소
                            </Button>
                            <Button
                              type="submit"
                              variant="contained"
                              size="small"
                              disabled={!replyContent.trim()}
                            >
                              대댓글 작성
                            </Button>
                          </Box>
                        </Box>
                      )}
                      
                      {/* 대댓글 목록 */}
                      {comment.replies && comment.replies.length > 0 && (
                        <List sx={{ width: '100%', pl: 4 }}>
                          {comment.replies.map((reply) => (
                            <ListItem key={reply.commentId} alignItems="flex-start">
                              <ListItemAvatar>
                                <Avatar sx={{ width: 32, height: 32 }}>{reply.nickname?.[0]}</Avatar>
                              </ListItemAvatar>
                              <ListItemText
                                primary={
                                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <Typography variant="subtitle2">{reply.nickname}</Typography>
                                    <Typography variant="caption" color="text.secondary">
                                      {new Date(reply.createAt).toLocaleString('ko-KR')}
                                    </Typography>
                                  </Box>
                                }
                                secondary={
                                  <Typography
                                    component="span"
                                    variant="body2"
                                    color="text.primary"
                                    sx={{ display: 'block', mt: 1 }}
                                  >
                                    {reply.content}
                                  </Typography>
                                }
                              />
                              {user && Number(user.userId) === reply.userId && (
                                <Button
                                  size="small"
                                  color="error"
                                  onClick={() => handleDeleteComment(reply.commentId)}
                                >
                                  삭제
                                </Button>
                              )}
                            </ListItem>
                          ))}
                        </List>
                      )}
                    </ListItem>
                    <Divider variant="inset" component="li" />
                  </React.Fragment>
                );
              })
            ) : (
              <ListItem>
                <ListItemText primary="댓글이 없습니다." />
              </ListItem>
            )}
          </List>
        </Box>
      </Paper>
    </Container>
  );
};

export default BoardDetail; 