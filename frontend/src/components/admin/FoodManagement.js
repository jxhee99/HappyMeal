import React, { useState, useEffect } from 'react';
import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  IconButton,
  Button,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Typography,
  Alert,
  CircularProgress,
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { foodService } from '../../services/foodService';

const FoodManagement = () => {
  const [foods, setFoods] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [selectedFood, setSelectedFood] = useState(null);
  const [editForm, setEditForm] = useState({
    name: '',
    category: '',
    servingSize: '',
    unit: '',
    calories: '',
    carbs: '',
    sugar: '',
    protein: '',
    fat: '',
    imgUrl: '',
    foodCode: '',
  });

  const fetchFoods = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await foodService.getAllFoods({
        page,
        size: rowsPerPage,
        sortBy: 'name ASC',
      });
      if (response.data) {
        setFoods(response.data.content || []);
        setTotalElements(response.data.totalElements || 0);
      }
    } catch (error) {
      setError('음식 목록을 불러오는데 실패했습니다.');
      console.error('Error fetching foods:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFoods();
  }, [page, rowsPerPage]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleEditClick = (food) => {
    setSelectedFood(food);
    setEditForm({
      name: food.name,
      category: food.category,
      servingSize: food.servingSize,
      unit: food.unit,
      calories: food.calories,
      carbs: food.carbs,
      sugar: food.sugar,
      protein: food.protein,
      fat: food.fat,
      imgUrl: food.imgUrl,
      foodCode: food.foodCode,
    });
    setEditDialogOpen(true);
  };

  const handleDeleteClick = async (foodId) => {
    if (window.confirm('정말로 이 음식을 삭제하시겠습니까?')) {
      try {
        await foodService.deleteFood(foodId);
        fetchFoods();
      } catch (error) {
        setError('음식 삭제에 실패했습니다.');
        console.error('Error deleting food:', error);
      }
    }
  };

  const handleEditSubmit = async () => {
    try {
      await foodService.updateFood(selectedFood.foodId, editForm);
      setEditDialogOpen(false);
      fetchFoods();
    } catch (error) {
      setError('음식 정보 수정에 실패했습니다.');
      console.error('Error updating food:', error);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchFoods();
      return;
    }
    try {
      setLoading(true);
      const response = await foodService.searchFoodsByName(searchTerm, {
        page,
        size: rowsPerPage,
        sortBy: 'name ASC',
      });
      if (response.data) {
        setFoods(response.data.content || []);
        setTotalElements(response.data.totalElements || 0);
      }
    } catch (error) {
      setError('음식 검색에 실패했습니다.');
      console.error('Error searching foods:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="음식 이름으로 검색"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
        />
        <Button variant="contained" onClick={handleSearch}>
          검색
        </Button>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>이름</TableCell>
                <TableCell>카테고리</TableCell>
                <TableCell>칼로리</TableCell>
                <TableCell>단백질</TableCell>
                <TableCell>지방</TableCell>
                <TableCell>탄수화물</TableCell>
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
                  <TableCell>{food.fat}</TableCell>
                  <TableCell>{food.carbs}</TableCell>
                  <TableCell>
                    <IconButton onClick={() => handleEditClick(food)}>
                      <EditIcon />
                    </IconButton>
                    <IconButton onClick={() => handleDeleteClick(food.foodId)}>
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        labelRowsPerPage="페이지당 행 수"
        labelDisplayedRows={({ from, to, count }) => `${from}-${to} / ${count}`}
      />

      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)}>
        <DialogTitle>음식 정보 수정</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
            <TextField
              label="이름"
              value={editForm.name}
              onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
            />
            <TextField
              label="카테고리"
              value={editForm.category}
              onChange={(e) => setEditForm({ ...editForm, category: e.target.value })}
            />
            <TextField
              label="1회 제공량"
              type="number"
              value={editForm.servingSize}
              onChange={(e) => setEditForm({ ...editForm, servingSize: e.target.value })}
            />
            <TextField
              label="단위"
              value={editForm.unit}
              onChange={(e) => setEditForm({ ...editForm, unit: e.target.value })}
            />
            <TextField
              label="칼로리"
              type="number"
              value={editForm.calories}
              onChange={(e) => setEditForm({ ...editForm, calories: e.target.value })}
            />
            <TextField
              label="탄수화물"
              type="number"
              value={editForm.carbs}
              onChange={(e) => setEditForm({ ...editForm, carbs: e.target.value })}
            />
            <TextField
              label="당류"
              type="number"
              value={editForm.sugar}
              onChange={(e) => setEditForm({ ...editForm, sugar: e.target.value })}
            />
            <TextField
              label="단백질"
              type="number"
              value={editForm.protein}
              onChange={(e) => setEditForm({ ...editForm, protein: e.target.value })}
            />
            <TextField
              label="지방"
              type="number"
              value={editForm.fat}
              onChange={(e) => setEditForm({ ...editForm, fat: e.target.value })}
            />
            <TextField
              label="이미지 URL"
              value={editForm.imgUrl}
              onChange={(e) => setEditForm({ ...editForm, imgUrl: e.target.value })}
            />
            <TextField
              label="음식 코드"
              value={editForm.foodCode}
              onChange={(e) => setEditForm({ ...editForm, foodCode: e.target.value })}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>취소</Button>
          <Button onClick={handleEditSubmit} variant="contained">
            저장
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default FoodManagement; 