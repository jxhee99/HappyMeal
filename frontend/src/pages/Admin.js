import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
  CircularProgress,
  Alert
} from '@mui/material';
import { foodRequestService } from '../services/FoodRequestService';
import { foodService } from '../services/foodService';
import { useAuth } from '../contexts/AuthContext';

const Admin = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [tabValue, setTabValue] = useState(0);
  const [selectedFood, setSelectedFood] = useState(null);
  const [isFoodDialogOpen, setIsFoodDialogOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 음식 요청 관련 상태
  const [foodRequests, setFoodRequests] = useState([]);
  const [foods, setFoods] = useState([]);

  // 관리자 권한 체크
  useEffect(() => {
    if (!user || user.role !== 'ADMIN') {
      navigate('/');
    }
  }, [user, navigate]);

  // 컴포넌트 마운트 시 초기 데이터 로드
  useEffect(() => {
    if (user?.role === 'ADMIN') {
      fetchFoodRequests();
    }
  }, [user]);

  // 음식 요청 목록 조회
  const fetchFoodRequests = async () => {
    try {
      setLoading(true);
      const data = await foodRequestService.getAllFoodRequests();
      setFoodRequests(data);
      setError(null);
    } catch (err) {
      setError('음식 요청 목록을 불러오는데 실패했습니다.');
      console.error('음식 요청 조회 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  // 음식 목록 조회
  const fetchFoods = async () => {
    try {
      setLoading(true);
      const data = await foodService.getFoods();
      setFoods(data);
      setError(null);
    } catch (err) {
      setError('음식 목록을 불러오는데 실패했습니다.');
      console.error('음식 조회 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  // 탭 변경 시 데이터 로드
  const handleTabChange = async (event, newValue) => {
    setTabValue(newValue);
    if (newValue === 0) {
      await fetchFoodRequests();
    } else if (newValue === 2) {
      await fetchFoods();
    }
  };

  // 음식 요청 승인
  const handleRequestApprove = async (requestId) => {
    try {
      await foodRequestService.updateFoodRequestStatus(requestId, 'APPROVED');
      await fetchFoodRequests();
    } catch (err) {
      console.error('요청 승인 실패:', err);
      alert('요청 승인에 실패했습니다.');
    }
  };

  // 음식 요청 거절
  const handleRequestReject = async (requestId) => {
    try {
      await foodRequestService.updateFoodRequestStatus(requestId, 'REJECTED');
      await fetchFoodRequests();
    } catch (err) {
      console.error('요청 거절 실패:', err);
      alert('요청 거절에 실패했습니다.');
    }
  };

  // 음식 수정
  const handleFoodEdit = (food) => {
    setSelectedFood(food);
    setIsFoodDialogOpen(true);
  };

  // 음식 삭제
  const handleFoodDelete = async (foodId) => {
    if (window.confirm('정말로 이 음식을 삭제하시겠습니까?')) {
      try {
        await foodService.deleteFood(foodId);
        await fetchFoods();
      } catch (err) {
        console.error('음식 삭제 실패:', err);
        alert('음식 삭제에 실패했습니다.');
      }
    }
  };

  // 상태에 따른 칩 컴포넌트
  const getStatusChip = (status) => {
    const statusConfig = {
      PENDING: { label: '대기중', color: 'warning' },
      APPROVED: { label: '승인', color: 'success' },
      REJECTED: { label: '거절', color: 'error' },
    };
    const config = statusConfig[status] || { label: '알 수 없음', color: 'default' };
    return <Chip label={config.label} color={config.color} size="small" />;
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box p={3}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Paper sx={{ p: 3, borderRadius: 2 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          관리자 페이지
        </Typography>
        
        <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
          <Tabs value={tabValue} onChange={handleTabChange}>
            <Tab label="음식 요청 관리" />
            <Tab label="사용자 관리" />
            <Tab label="음식 관리" />
          </Tabs>
        </Box>

        {/* 음식 요청 관리 */}
        {tabValue === 0 && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>음식명</TableCell>
                  <TableCell>카테고리</TableCell>
                  <TableCell>칼로리</TableCell>
                  <TableCell>영양소</TableCell>
                  <TableCell>상태</TableCell>
                  <TableCell>작업</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {foodRequests.map((request) => (
                  <TableRow key={request.foodRequestId}>
                    <TableCell>{request.foodRequestId}</TableCell>
                    <TableCell>{request.name}</TableCell>
                    <TableCell>{request.category}</TableCell>
                    <TableCell>{request.calories} kcal</TableCell>
                    <TableCell>
                      탄수화물: {request.carbs}g<br />
                      당류: {request.sugar}g<br />
                      단백질: {request.protein}g<br />
                      지방: {request.fat}g
                    </TableCell>
                    <TableCell>{getStatusChip(request.isRegistered)}</TableCell>
                    <TableCell>
                      {request.isRegistered === 'PENDING' && (
                        <Box>
                          <Button
                            size="small"
                            color="success"
                            onClick={() => handleRequestApprove(request.foodRequestId)}
                            sx={{ mr: 1 }}
                          >
                            승인
                          </Button>
                          <Button
                            size="small"
                            color="error"
                            onClick={() => handleRequestReject(request.foodRequestId)}
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
                  <TableRow key={food.foodId}>
                    <TableCell>{food.foodId}</TableCell>
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
                        sx={{ mr: 1 }}
                      >
                        수정
                      </Button>
                      <Button
                        size="small"
                        color="error"
                        onClick={() => handleFoodDelete(food.foodId)}
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

export default Admin; 