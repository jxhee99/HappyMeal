import React, { useState } from 'react';
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
} from '@mui/material';
import {
  Edit as EditIcon,
  Delete as DeleteIcon,
  Favorite as FavoriteIcon,
  FavoriteBorder as FavoriteBorderIcon,
} from '@mui/icons-material';

const MyPage = () => {
  const [tabValue, setTabValue] = useState(0);
  const [nickname, setNickname] = useState('사용자1');
  const [isEditing, setIsEditing] = useState(false);

  // TODO: API 연동
  const [foodRequests] = useState([
    {
      id: 1,
      name: '샘플 음식 1',
      status: 'PENDING',
      date: '2024-03-20',
    },
  ]);

  const [myPosts] = useState([
    {
      id: 1,
      title: '내가 쓴 게시글 1',
      category: '맛집 추천',
      date: '2024-03-20',
    },
  ]);

  const [myComments] = useState([
    {
      id: 1,
      postId: 1,
      content: '댓글 내용 1',
      date: '2024-03-20',
    },
  ]);

  const [likedPosts] = useState([
    {
      id: 1,
      title: '좋아요한 게시글 1',
      category: '맛집 추천',
      date: '2024-03-20',
    },
  ]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleNicknameEdit = async () => {
    if (isEditing) {
      // TODO: API 연동
      try {
        // await api.put('/user/nickname', { nickname });
        setIsEditing(false);
      } catch (error) {
        console.error('닉네임 수정 실패:', error);
      }
    } else {
      setIsEditing(true);
    }
  };

  const getStatusChip = (status) => {
    const statusConfig = {
      PENDING: { label: '대기중', color: 'warning' },
      APPROVED: { label: '승인', color: 'success' },
      REJECTED: { label: '거절', color: 'error' },
    };
    const config = statusConfig[status];
    return <Chip label={config.label} color={config.color} size="small" />;
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Grid container spacing={3}>
        {/* 개인정보 */}
        <Grid item xs={12}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h5" gutterBottom>
              개인정보
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              {isEditing ? (
                <TextField
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  size="small"
                />
              ) : (
                <Typography variant="h6">{nickname}</Typography>
              )}
              <Button
                variant="outlined"
                size="small"
                onClick={handleNicknameEdit}
              >
                {isEditing ? '저장' : '수정'}
              </Button>
            </Box>
          </Paper>
        </Grid>

        {/* 탭 메뉴 */}
        <Grid item xs={12}>
          <Paper sx={{ width: '100%' }}>
            <Tabs
              value={tabValue}
              onChange={handleTabChange}
              indicatorColor="primary"
              textColor="primary"
              variant="fullWidth"
            >
              <Tab label="음식 등록 요청" />
              <Tab label="작성한 글" />
              <Tab label="작성한 댓글" />
              <Tab label="좋아요" />
            </Tabs>

            {/* 음식 등록 요청 */}
            {tabValue === 0 && (
              <List>
                {foodRequests.map((request) => (
                  <ListItem key={request.id}>
                    <ListItemText
                      primary={request.name}
                      secondary={request.date}
                    />
                    <ListItemSecondaryAction>
                      {getStatusChip(request.status)}
                    </ListItemSecondaryAction>
                  </ListItem>
                ))}
              </List>
            )}

            {/* 작성한 글 */}
            {tabValue === 1 && (
              <List>
                {myPosts.map((post) => (
                  <ListItem key={post.id}>
                    <ListItemText
                      primary={post.title}
                      secondary={`${post.category} | ${post.date}`}
                    />
                    <ListItemSecondaryAction>
                      <IconButton edge="end" aria-label="edit">
                        <EditIcon />
                      </IconButton>
                      <IconButton edge="end" aria-label="delete">
                        <DeleteIcon />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                ))}
              </List>
            )}

            {/* 작성한 댓글 */}
            {tabValue === 2 && (
              <List>
                {myComments.map((comment) => (
                  <ListItem key={comment.id}>
                    <ListItemText
                      primary={comment.content}
                      secondary={comment.date}
                    />
                    <ListItemSecondaryAction>
                      <IconButton edge="end" aria-label="edit">
                        <EditIcon />
                      </IconButton>
                      <IconButton edge="end" aria-label="delete">
                        <DeleteIcon />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                ))}
              </List>
            )}

            {/* 좋아요 */}
            {tabValue === 3 && (
              <List>
                {likedPosts.map((post) => (
                  <ListItem key={post.id}>
                    <ListItemText
                      primary={post.title}
                      secondary={`${post.category} | ${post.date}`}
                    />
                    <ListItemSecondaryAction>
                      <IconButton edge="end" aria-label="unlike">
                        <FavoriteIcon color="error" />
                      </IconButton>
                    </ListItemSecondaryAction>
                  </ListItem>
                ))}
              </List>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default MyPage; 