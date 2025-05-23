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
import { CheckIcon, CloseIcon } from '@mui/icons-material';

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

  const pastelStatus = {
    PENDING: { label: '대기중', sx: { bgcolor: '#ffe0b2', color: '#222', borderRadius: 1 } },
    APPROVED: { label: '승인', sx: { bgcolor: '#b2dfdb', color: '#222', borderRadius: 1 } },
    REJECTED: { label: '거절', sx: { bgcolor: '#ffcdd2', color: '#222 !important', borderRadius: 1 } },
  };

  const getStatusChip = (status) => {
    const config = pastelStatus[status] || { label: '알 수 없음', sx: { borderRadius: 1 } };
    return <Chip label={config.label} size="small" sx={config.sx} />;
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
              <TableCell align="center">ID</TableCell>
              <TableCell align="center">음식 이름</TableCell>
              <TableCell align="center">카테고리</TableCell>
              <TableCell align="center">칼로리</TableCell>
              <TableCell align="center">영양소</TableCell>
              <TableCell align="center">상태</TableCell>
              <TableCell align="center">작업</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {requests.map((request) => (
              <TableRow key={request.foodRequestId}>
                <TableCell align="center">{request.foodRequestId}</TableCell>
                <TableCell align="center">{request.name}</TableCell>
                <TableCell align="center">{request.category}</TableCell>
                <TableCell align="center">{request.calories} kcal</TableCell>
                <TableCell align="center">
                  탄수화물: {request.carbs}g<br />
                  당류: {request.sugar}g<br />
                  단백질: {request.protein}g<br />
                  지방: {request.fat}g
                </TableCell>
                <TableCell align="center">{getStatusChip(request.isRegistered)}</TableCell>
                <TableCell align="center">
                  {request.isRegistered === 'PENDING' && (
                    <>
                      <Button
                        variant="contained"
                        color="success"
                        size="small"
                        startIcon={<CheckIcon />}
                        onClick={() => handleStatusUpdate(request.foodRequestId, 'APPROVED')}
                        sx={{
                          mr: 1,
                          backgroundColor: '#e3f2fd',
                          color: '#1976d2',
                          '&:hover': {
                            backgroundColor: '#90caf9',
                            color: '#1565c0',
                          },
                          boxShadow: 'none',
                        }}
                      >
                        승인
                      </Button>
                      <Button
                        variant="contained"
                        color="error"
                        size="small"
                        startIcon={<CloseIcon />}
                        onClick={() => handleStatusUpdate(request.foodRequestId, 'REJECTED')}
                        sx={{
                          backgroundColor: '#e3f2fd',
                          color: '#1976d2',
                          '&:hover': {
                            backgroundColor: '#90caf9',
                            color: '#1565c0',
                          },
                          boxShadow: 'none',
                        }}
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