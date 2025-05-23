import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Tabs,
  Tab,
  TextField,
  Button,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Chip,
  CircularProgress,
  Pagination,
  Alert,
  Avatar,
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Favorite as FavoriteIcon,
  FavoriteBorder as FavoriteBorderIcon,
  Restaurant as RestaurantIcon,
} from '@mui/icons-material';
import axios from '../services/axiosConfig';

const MyPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const auth = useAuth();
  const [tabValue, setTabValue] = useState(0);
  const [profile, setProfile] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [newNickname, setNewNickname] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // 페이지네이션 상태
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [itemsPerPage] = useState(10);

  // 데이터 상태
  const [posts, setPosts] = useState([]);
  const [comments, setComments] = useState([]);
  const [likes, setLikes] = useState([]);
  const [foodRequests, setFoodRequests] = useState([]);

  // 프로필 정보 조회
  const fetchProfile = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/mypages/profile');
      setProfile(response.data);
      setNewNickname(response.data.nickName);
    } catch (err) {
      setError('프로필 정보를 불러오는데 실패했습니다.');
      console.error('프로필 조회 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  // 게시글 목록 조회
  const fetchPosts = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/mypages/posts?page=${page - 1}&size=${itemsPerPage}`);
      console.log('게시글 목록 응답 데이터:', response.data.content); // 각 게시글의 상세 데이터 확인
      setPosts(response.data.content || []);
      setTotalPages(Math.ceil((response.data.totalElements || 0) / itemsPerPage));
    } catch (err) {
      setError('게시글 목록을 불러오는데 실패했습니다.');
      console.error('게시글 조회 실패:', err);
      setPosts([]);
    } finally {
      setLoading(false);
    }
  };

  // 댓글 목록 조회
  const fetchComments = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/mypages/comments?page=${page - 1}&size=${itemsPerPage}`);
      console.log('댓글 목록 응답:', response.data); // 디버깅용 로그 추가
      setComments(response.data.content || []);
      setTotalPages(Math.ceil((response.data.totalElements || 0) / itemsPerPage));
    } catch (err) {
      setError('댓글 목록을 불러오는데 실패했습니다.');
      console.error('댓글 조회 실패:', err);
      setComments([]);
    } finally {
      setLoading(false);
    }
  };

  // 좋아요 목록 조회
  const fetchLikes = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/mypages/likes?page=${page - 1}&size=${itemsPerPage}`);
      console.log('좋아요 목록 응답 데이터:', response.data.content); // 각 게시글의 상세 데이터 확인
      setLikes(response.data.content || []);
      setTotalPages(Math.ceil((response.data.totalElements || 0) / itemsPerPage));
    } catch (err) {
      setError('좋아요 목록을 불러오는데 실패했습니다.');
      console.error('좋아요 조회 실패:', err);
      setLikes([]);
    } finally {
      setLoading(false);
    }
  };

  // 음식 등록 요청 목록 조회
  const fetchFoodRequests = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/food-requests/my?page=${page - 1}&size=${itemsPerPage}`);
      console.log('음식 등록 요청 목록 응답:', response.data);
      setFoodRequests(response.data || []);
      setTotalPages(1);
    } catch (err) {
      setError('음식 등록 요청 목록을 불러오는데 실패했습니다.');
      console.error('음식 등록 요청 조회 실패:', err);
      setFoodRequests([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!auth.isAuthenticated) {
      navigate('/login');
      return;
    }
    fetchProfile();
  }, [auth.isAuthenticated]);

  useEffect(() => {
    if (!auth.isAuthenticated) return;
    
    setPage(1);
    switch (tabValue) {
      case 0:
        fetchPosts();
        break;
      case 1:
        fetchComments();
        break;
      case 2:
        fetchLikes();
        break;
      case 3:
        fetchFoodRequests();
        break;
      default:
        break;
    }
  }, [tabValue]);

  useEffect(() => {
    if (!auth.isAuthenticated) return;
    
    switch (tabValue) {
      case 0:
        fetchPosts();
        break;
      case 1:
        fetchComments();
        break;
      case 2:
        fetchLikes();
        break;
      case 3:
        fetchFoodRequests();
        break;
      default:
        break;
    }
  }, [page]);

  useEffect(() => {
    // URL의 tab 파라미터 확인
    const params = new URLSearchParams(location.search);
    const tabParam = params.get('tab');
    if (tabParam !== null) {
      const tabIndex = parseInt(tabParam);
      if (!isNaN(tabIndex) && tabIndex >= 0 && tabIndex <= 3) {
        setTabValue(tabIndex);
      }
    }
  }, [location.search]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleNicknameEdit = async () => {
    if (isEditing) {
      try {
        await axios.put('/mypages/profile', {
          nickname: newNickname,
          profileImgUrl: profile.profileImgUrl
        });
        await fetchProfile();
        setIsEditing(false);
      } catch (err) {
        setError('닉네임 수정에 실패했습니다.');
        console.error('닉네임 수정 실패:', err);
      }
    } else {
      setIsEditing(true);
    }
  };

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      
      <Grid container spacing={3}>
        {/* 개인정보 */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3, borderRadius: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
              <Avatar
                src={profile?.profileImgUrl}
                alt={profile?.nickName}
                sx={{ width: 100, height: 100 }}
              />
              <Box sx={{ flexGrow: 1 }}>
                <Typography variant="h5" gutterBottom>
                  프로필 정보
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  {isEditing ? (
                    <TextField
                      value={newNickname}
                      onChange={(e) => setNewNickname(e.target.value)}
                      size="small"
                      variant="outlined"
                    />
                  ) : (
                    <Typography variant="h6">{profile?.nickName}</Typography>
                  )}
                  <Button
                    variant="contained"
                    size="small"
                    onClick={handleNicknameEdit}
                    sx={{ 
                      backgroundColor: isEditing ? '#4CAF50' : '#2196F3',
                      '&:hover': {
                        backgroundColor: isEditing ? '#45a049' : '#1976D2',
                      }
                    }}
                  >
                    {isEditing ? '저장' : '수정'}
                  </Button>
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  {profile?.email}
                </Typography>
              </Box>
            </Box>
          </Paper>
        </Grid>

        {/* 탭 메뉴 */}
        <Grid item xs={12}>
          <Paper sx={{ width: '100%', borderRadius: 2 }}>
            <Tabs
              value={tabValue}
              onChange={handleTabChange}
              indicatorColor="primary"
              textColor="primary"
              variant="fullWidth"
              sx={{ borderBottom: 1, borderColor: 'divider' }}
            >
              <Tab label="작성한 글" />
              <Tab label="작성한 댓글" />
              <Tab label="좋아요" />
              <Tab label="음식 등록 요청" />
            </Tabs>

            {/* 작성한 글 */}
            {tabValue === 0 && (
              <Box sx={{ p: 2 }}>
                {posts?.length > 0 ? (
                  <>
                    <List>
                      {posts.map((post) => (
                        <ListItem
                          key={post.boardId}
                          sx={{
                            borderBottom: '1px solid #eee',
                            '&:last-child': { borderBottom: 'none' }
                          }}
                        >
                          <ListItemText
                            primary={
                              <Typography
                                variant="subtitle1"
                                sx={{ cursor: 'pointer' }}
                                onClick={() => navigate(`/board/${post.boardId}`)}
                              >
                                {post.title}
                              </Typography>
                            }
                            secondary={
                              <Typography component="span" variant="body2">
                                <Box sx={{ display: 'flex', gap: 2, mt: 1 }}>
                                  <Chip
                                    size="small"
                                    label={`조회수 ${post.views || 0}`}
                                    variant="outlined"
                                  />
                                  <Chip
                                    size="small"
                                    label={`좋아요 ${post.likesCount || 0}`}
                                    variant="outlined"
                                  />
                                  <Chip
                                    size="small"
                                    label={`댓글 ${post.commentsCount || post.comments_count || 0}`}
                                    variant="outlined"
                                  />
                                  <Typography component="span" variant="body2" color="text.secondary">
                                    {formatDate(post.createAt)}
                                  </Typography>
                                </Box>
                              </Typography>
                            }
                          />
                        </ListItem>
                      ))}
                    </List>
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                      <Pagination
                        count={totalPages}
                        page={page}
                        onChange={handlePageChange}
                        color="primary"
                      />
                    </Box>
                  </>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography variant="body1" color="text.secondary">
                      작성한 글이 없습니다.
                    </Typography>
                  </Box>
                )}
              </Box>
            )}

            {/* 작성한 댓글 */}
            {tabValue === 1 && (
              <Box sx={{ p: 2 }}>
                {comments?.length > 0 ? (
                  <>
                    <List>
                      {comments.map((comment) => (
                        <ListItem
                          key={comment.commentId}
                          sx={{
                            borderBottom: '1px solid #eee',
                            '&:last-child': { borderBottom: 'none' },
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'flex-start',
                            gap: 1
                          }}
                        >
                          <Box sx={{ width: '100%' }}>
                            <Typography
                              variant="subtitle1"
                              sx={{ 
                                cursor: 'pointer',
                                color: 'primary.main',
                                fontWeight: 500,
                                mb: 1
                              }}
                              onClick={() => navigate(`/board/${comment.boardId}`)}
                            >
                              {comment.boardTitle || '게시글 보기'}
                            </Typography>
                            <Typography
                              variant="body1"
                              sx={{
                                backgroundColor: 'grey.50',
                                p: 2,
                                borderRadius: 1,
                                width: '100%'
                              }}
                            >
                              {comment.content}
                            </Typography>
                            <Box sx={{ 
                              display: 'flex', 
                              gap: 2, 
                              mt: 1,
                              alignItems: 'center'
                            }}>
                              <Chip
                                size="small"
                                label={comment.parentCommentId ? '대댓글' : '댓글'}
                                color={comment.parentCommentId ? 'secondary' : 'primary'}
                                variant="outlined"
                              />
                              <Typography variant="body2" color="text.secondary">
                                {formatDate(comment.createAt)}
                              </Typography>
                              {comment.boardCommentsCount !== undefined && (
                                <Chip
                                  size="small"
                                  label={`댓글 ${comment.boardCommentsCount}`}
                                  variant="outlined"
                                />
                              )}
                            </Box>
                          </Box>
                        </ListItem>
                      ))}
                    </List>
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                      <Pagination
                        count={totalPages}
                        page={page}
                        onChange={handlePageChange}
                        color="primary"
                      />
                    </Box>
                  </>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography variant="body1" color="text.secondary">
                      작성한 댓글이 없습니다.
                    </Typography>
                  </Box>
                )}
              </Box>
            )}

            {/* 좋아요 */}
            {tabValue === 2 && (
              <Box sx={{ p: 2 }}>
                {likes?.length > 0 ? (
                  <>
                    <List>
                      {likes.map((post) => (
                        <ListItem
                          key={post.boardId}
                          sx={{
                            borderBottom: '1px solid #eee',
                            '&:last-child': { borderBottom: 'none' }
                          }}
                        >
                          <ListItemText
                            primary={
                              <Typography
                                variant="subtitle1"
                                sx={{ cursor: 'pointer' }}
                                onClick={() => navigate(`/board/${post.boardId}`)}
                              >
                                {post.title}
                              </Typography>
                            }
                            secondary={
                              <Typography component="span" variant="body2">
                                <Box sx={{ display: 'flex', gap: 2, mt: 1 }}>
                                  <Chip
                                    size="small"
                                    label={`조회수 ${post.views || 0}`}
                                    variant="outlined"
                                  />
                                  <Chip
                                    size="small"
                                    label={`좋아요 ${post.likesCount || 0}`}
                                    variant="outlined"
                                  />
                                  <Chip
                                    size="small"
                                    label={`댓글 ${post.commentsCount || post.comments_count || 0}`}
                                    variant="outlined"
                                  />
                                  <Typography component="span" variant="body2" color="text.secondary">
                                    {formatDate(post.createAt)}
                                  </Typography>
                                </Box>
                              </Typography>
                            }
                          />
                          <ListItemSecondaryAction>
                            <IconButton 
                              edge="end" 
                              aria-label="unlike"
                              sx={{ 
                                color: '#fb3d62',
                                '&:hover': { color: '#fb3d62' }
                              }}
                            >
                              <FavoriteIcon />
                            </IconButton>
                          </ListItemSecondaryAction>
                        </ListItem>
                      ))}
                    </List>
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
                      <Pagination
                        count={totalPages}
                        page={page}
                        onChange={handlePageChange}
                        color="primary"
                      />
                    </Box>
                  </>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography variant="body1" color="text.secondary">
                      좋아요한 글이 없습니다.
                    </Typography>
                  </Box>
                )}
              </Box>
            )}

            {/* 음식 등록 요청 */}
            {tabValue === 3 && (
              <Box sx={{ p: 2 }}>
                {foodRequests?.length > 0 ? (
                  <>
                    <List>
                      {foodRequests.map((request) => (
                        <ListItem
                          key={request.foodRequestId}
                          sx={{
                            borderBottom: '1px solid #eee',
                            '&:last-child': { borderBottom: 'none' }
                          }}
                        >
                          <ListItemText
                            primary={
                              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <Typography variant="subtitle1">
                                  {request.name}
                                </Typography>
                                <Chip
                                  size="small"
                                  label={
                                    request.isRegistered === 'PENDING' ? '대기중' :
                                    request.isRegistered === 'APPROVED' ? '승인됨' :
                                    request.isRegistered === 'REJECTED' ? '거절됨' : '알 수 없음'
                                  }
                                  color={
                                    request.isRegistered === 'PENDING' ? 'warning' :
                                    request.isRegistered === 'APPROVED' ? 'success' :
                                    request.isRegistered === 'REJECTED' ? 'error' : 'default'
                                  }
                                  variant="outlined"
                                />
                              </Box>
                            }
                            secondary={
                              <Typography component="span" variant="body2">
                                <Box sx={{ display: 'flex', gap: 2, mt: 1 }}>
                                  <Chip
                                    size="small"
                                    label={`카테고리: ${request.category}`}
                                    variant="outlined"
                                  />
                                  <Chip
                                    size="small"
                                    label={`1회 제공량: ${request.servingSize}${request.unit}`}
                                    variant="outlined"
                                  />
                                  <Chip
                                    size="small"
                                    label={`칼로리: ${request.calories}kcal`}
                                    variant="outlined"
                                  />
                                  <Typography component="span" variant="body2" color="text.secondary">
                                    {formatDate(request.createAt)}
                                  </Typography>
                                </Box>
                              </Typography>
                            }
                          />
                        </ListItem>
                      ))}
                    </List>
                  </>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography variant="body1" color="text.secondary">
                      등록한 음식 요청이 없습니다.
                    </Typography>
                  </Box>
                )}
              </Box>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default MyPage; 