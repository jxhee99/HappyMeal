import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Typography,
  CircularProgress,
  Chip,
  Alert
} from '@mui/material';
import { foodRequestService } from '../../services/FoodRequestService';

export default function FoodRequestManagement() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const data = await foodRequestService.getAllFoodRequests();
      setRequests(data);
      setError(null);
    } catch (err) {
      setError('음식 요청 목록을 불러오는데 실패했습니다.');
      console.error('음식 요청 조회 실패:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (requestId, newStatus) => {
    try {
      await foodRequestService.updateFoodRequestStatus(requestId, newStatus);
      fetchRequests(); // 목록 새로고침
    } catch (err) {
      console.error('상태 업데이트 실패:', err);
      alert('상태 업데이트에 실패했습니다.');
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
    <Box p={3}>
      <Typography variant="h5" gutterBottom>
        음식 추가 요청 관리
      </Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>음식 이름</TableCell>
              <TableCell>카테고리</TableCell>
              <TableCell>칼로리</TableCell>
              <TableCell>영양소</TableCell>
              <TableCell>상태</TableCell>
              <TableCell>작업</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {requests.map((request) => (
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
                    <>
                      <Button
                        variant="contained"
                        color="success"
                        size="small"
                        onClick={() => handleStatusUpdate(request.foodRequestId, 'APPROVED')}
                        sx={{ mr: 1 }}
                      >
                        승인
                      </Button>
                      <Button
                        variant="contained"
                        color="error"
                        size="small"
                        onClick={() => handleStatusUpdate(request.foodRequestId, 'REJECTED')}
                      >
                        거절
                      </Button>
                    </>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
} 