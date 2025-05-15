import React, { useState } from 'react';
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Tabs,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';

const AdminPage = () => {
  const [tabValue, setTabValue] = useState(0);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [selectedFood, setSelectedFood] = useState(null);
  const [isRequestDialogOpen, setIsRequestDialogOpen] = useState(false);
  const [isFoodDialogOpen, setIsFoodDialogOpen] = useState(false);

  // TODO: API 연동
  const [foodRequests] = useState([
    {
      id: 1,
      name: '샘플 음식 1',
      category: '과일',
      status: 'PENDING',
      user: '사용자1',
      date: '2024-03-20',
    },
  ]);

  const [users] = useState([
    {
      id: 1,
      nickname: '사용자1',
      email: 'user1@example.com',
      role: 'ROLE_USER',
      joinDate: '2024-03-20',
    },
  ]);

  const [foods] = useState([
    {
      id: 1,
      name: '샘플 음식 1',
      category: '과일',
      calories: 100,
      protein: 10,
      carbs: 20,
      fat: 5,
    },
  ]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleRequestApprove = async (requestId) => {
    // TODO: API 연동
    console.log('요청 승인:', requestId);
  };

  const handleRequestReject = async (requestId) => {
    // TODO: API 연동
    console.log('요청 거절:', requestId);
  };

  const handleUserDelete = async (userId) => {
    // TODO: API 연동
    console.log('사용자 삭제:', userId);
  };

  const handleFoodEdit = (food) => {
    setSelectedFood(food);
    setIsFoodDialogOpen(true);
  };

  const handleFoodDelete = async (foodId) => {
    // TODO: API 연동
    console.log('음식 삭제:', foodId);
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
              <Tab label="사용자 관리" />
              <Tab label="음식 관리" />
            </Tabs>

            {/* 음식 등록 요청 */}
            {tabValue === 0 && (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>ID</TableCell>
                      <TableCell>음식명</TableCell>
                      <TableCell>카테고리</TableCell>
                      <TableCell>요청자</TableCell>
                      <TableCell>요청일</TableCell>
                      <TableCell>상태</TableCell>
                      <TableCell>작업</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {foodRequests.map((request) => (
                      <TableRow key={request.id}>
                        <TableCell>{request.id}</TableCell>
                        <TableCell>{request.name}</TableCell>
                        <TableCell>{request.category}</TableCell>
                        <TableCell>{request.user}</TableCell>
                        <TableCell>{request.date}</TableCell>
                        <TableCell>{getStatusChip(request.status)}</TableCell>
                        <TableCell>
                          {request.status === 'PENDING' && (
                            <Box>
                              <Button
                                size="small"
                                color="success"
                                onClick={() => handleRequestApprove(request.id)}
                              >
                                승인
                              </Button>
                              <Button
                                size="small"
                                color="error"
                                onClick={() => handleRequestReject(request.id)}
                              >
                                거절
                              </Button>
                            </Box>
                          )}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

            {/* 사용자 관리 */}
            {tabValue === 1 && (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>ID</TableCell>
                      <TableCell>닉네임</TableCell>
                      <TableCell>이메일</TableCell>
                      <TableCell>권한</TableCell>
                      <TableCell>가입일</TableCell>
                      <TableCell>작업</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {users.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell>{user.id}</TableCell>
                        <TableCell>{user.nickname}</TableCell>
                        <TableCell>{user.email}</TableCell>
                        <TableCell>{user.role}</TableCell>
                        <TableCell>{user.joinDate}</TableCell>
                        <TableCell>
                          <Button
                            size="small"
                            color="error"
                            onClick={() => handleUserDelete(user.id)}
                          >
                            삭제
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

            {/* 음식 관리 */}
            {tabValue === 2 && (
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>ID</TableCell>
                      <TableCell>음식명</TableCell>
                      <TableCell>카테고리</TableCell>
                      <TableCell>칼로리</TableCell>
                      <TableCell>단백질</TableCell>
                      <TableCell>탄수화물</TableCell>
                      <TableCell>지방</TableCell>
                      <TableCell>작업</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {foods.map((food) => (
                      <TableRow key={food.id}>
                        <TableCell>{food.id}</TableCell>
                        <TableCell>{food.name}</TableCell>
                        <TableCell>{food.category}</TableCell>
                        <TableCell>{food.calories}</TableCell>
                        <TableCell>{food.protein}</TableCell>
                        <TableCell>{food.carbs}</TableCell>
                        <TableCell>{food.fat}</TableCell>
                        <TableCell>
                          <Button
                            size="small"
                            onClick={() => handleFoodEdit(food)}
                          >
                            수정
                          </Button>
                          <Button
                            size="small"
                            color="error"
                            onClick={() => handleFoodDelete(food.id)}
                          >
                            삭제
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Paper>
        </Grid>
      </Grid>

      {/* 음식 수정 다이얼로그 */}
      <Dialog open={isFoodDialogOpen} onClose={() => setIsFoodDialogOpen(false)}>
        <DialogTitle>음식 정보 수정</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
            <TextField
              label="음식명"
              value={selectedFood?.name || ''}
              fullWidth
            />
            <TextField
              label="카테고리"
              value={selectedFood?.category || ''}
              fullWidth
            />
            <TextField
              label="칼로리"
              type="number"
              value={selectedFood?.calories || ''}
              fullWidth
            />
            <TextField
              label="단백질"
              type="number"
              value={selectedFood?.protein || ''}
              fullWidth
            />
            <TextField
              label="탄수화물"
              type="number"
              value={selectedFood?.carbs || ''}
              fullWidth
            />
            <TextField
              label="지방"
              type="number"
              value={selectedFood?.fat || ''}
              fullWidth
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setIsFoodDialogOpen(false)}>취소</Button>
          <Button variant="contained" color="primary">
            저장
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AdminPage;