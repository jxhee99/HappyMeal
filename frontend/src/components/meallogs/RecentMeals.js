import React, { useState, useEffect, useRef } from 'react';
import {
  Box,
  Typography,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  Divider,
  Chip,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Grid,
  Autocomplete,
  IconButton,
  Menu,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@mui/material';
import { format, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';
import { mealLogService } from '../../services/mealLogService';
import { foodService } from '../../services/foodService';
import { imageService } from '../../services/imageService';
import PhotoCameraIcon from '@mui/icons-material/PhotoCamera';
import MoreVertIcon from '@mui/icons-material/MoreVert';

// 기본 이미지 URL
const DEFAULT_FOOD_IMAGE = 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=1000&auto=format&fit=crop';

// 식단 타입 매핑
const MEAL_TYPE_MAP = {
  'BREAKFAST': '아침',
  'LUNCH': '점심',
  'DINNER': '저녁',
  'SNACK': '간식'
};

// 역방향 매핑
const REVERSE_MEAL_TYPE_MAP = {
  '아침': 'BREAKFAST',
  '점심': 'LUNCH',
  '저녁': 'DINNER',
  '간식': 'SNACK'
};

const MEAL_TYPES = ['아침', '점심', '저녁', '간식'];

const RecentMeals = ({ selectedDate, mealLogs, onMealLogAdded }) => {
  const [open, setOpen] = useState(false);
  const [selectedMealType, setSelectedMealType] = useState('');
  const [foods, setFoods] = useState([]);
  const [selectedFood, setSelectedFood] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [isEditMode, setIsEditMode] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedMealLog, setSelectedMealLog] = useState(null);
  const [uploadingImage, setUploadingImage] = useState(false);
  const fileInputRef = useRef(null);
  const [newMealLog, setNewMealLog] = useState({
    foodId: '',
    foodName: '',
    quantity: 100,
    mealType: '',
    mealDate: selectedDate,
    imgUrl: ''
  });

  // 음식 검색
  useEffect(() => {
    const searchFoods = async () => {
      if (searchTerm.length > 0) {
        try {
          const results = await foodService.searchFoods(searchTerm);
          setFoods(results || []);
        } catch (error) {
          console.error('음식 검색 실패:', error);
          setFoods([]);
        }
      } else {
        setFoods([]);
      }
    };

    const debounceTimer = setTimeout(searchFoods, 300);
    return () => clearTimeout(debounceTimer);
  }, [searchTerm]);

  const handleOpen = (mealType) => {
    setSelectedMealType(mealType);
    setNewMealLog(prev => ({ 
      ...prev, 
      mealType: REVERSE_MEAL_TYPE_MAP[mealType],
      mealDate: selectedDate 
    }));
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedFood(null);
    setSearchTerm('');
    setIsEditMode(false);
    setSelectedMealLog(null);
    setNewMealLog({
      foodId: '',
      foodName: '',
      quantity: 100,
      mealType: '',
      mealDate: selectedDate,
      imgUrl: ''
    });
  };

  const handleFoodSelect = (event, value) => {
    if (value) {
      setSelectedFood(value);
      setNewMealLog(prev => ({
        ...prev,
        foodId: value.foodId,
        foodName: value.name,
        imgUrl: value.imgUrl || ''
      }));
    }
  };

  const handleQuantityChange = (e) => {
    const quantity = Number(e.target.value);
    setNewMealLog(prev => ({ ...prev, quantity }));
  };

  const calculateNutrition = () => {
    if (!selectedFood) return null;

    const ratio = newMealLog.quantity / 100;
    return {
      calories: (selectedFood.calories * ratio).toFixed(1),
      carbs: (selectedFood.carbs * ratio).toFixed(1),
      sugar: (selectedFood.sugar * ratio).toFixed(1),
      protein: (selectedFood.protein * ratio).toFixed(1),
      fat: (selectedFood.fat * ratio).toFixed(1)
    };
  };

  const handleMenuOpen = (event, mealLog) => {
    setAnchorEl(event.currentTarget);
    setSelectedMealLog(mealLog);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedMealLog(null);
  };

  const handleEdit = () => {
    if (selectedMealLog) {
      console.log('선택된 식단:', selectedMealLog);
      
      setSelectedFood({
        foodId: selectedMealLog.foodId,
        name: selectedMealLog.foodName,
        imgUrl: selectedMealLog.imgUrl,
        calories: selectedMealLog.calories,
        carbs: selectedMealLog.carbs,
        sugar: selectedMealLog.sugar,
        protein: selectedMealLog.protein,
        fat: selectedMealLog.fat
      });

      // 날짜가 없는 경우 현재 날짜 사용
      const mealDate = selectedMealLog.mealDate || selectedDate;
      console.log('설정할 날짜:', mealDate);

      setNewMealLog({
        foodId: selectedMealLog.foodId,
        foodName: selectedMealLog.foodName,
        quantity: selectedMealLog.quantity,
        mealType: selectedMealLog.mealType,
        mealDate: mealDate,
        imgUrl: selectedMealLog.imgUrl || '',
        logId: selectedMealLog.logId
      });
      setIsEditMode(true);
      setOpen(true);
    }
    handleMenuClose();
  };

  const handleDelete = async () => {
    if (selectedMealLog) {
      try {
        await mealLogService.deleteMealLog(selectedMealLog.logId);
        if (onMealLogAdded) {
          onMealLogAdded();
        }
      } catch (error) {
        console.error('식단 삭제 실패:', error);
        alert('식단 삭제에 실패했습니다.');
      }
    }
    handleMenuClose();
  };

  const handleImageUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    try {
      setUploadingImage(true);
      const result = await imageService.uploadImage(file);
      setNewMealLog(prev => ({
        ...prev,
        imgUrl: result.imageUrl
      }));
    } catch (error) {
      console.error('이미지 업로드 실패:', error);
      alert('이미지 업로드에 실패했습니다.');
    } finally {
      setUploadingImage(false);
    }
  };

  const handleImageClick = () => {
    fileInputRef.current.click();
  };

  const handleSubmit = async () => {
    try {
      console.log('현재 newMealLog:', newMealLog);
      
      // 날짜가 없는 경우 현재 날짜 사용
      const mealDate = newMealLog.mealDate || selectedDate;
      console.log('사용할 날짜:', mealDate);

      const mealLogData = {
        foodId: newMealLog.foodId,
        mealDate: mealDate,
        mealType: newMealLog.mealType,
        quantity: Number(newMealLog.quantity),
        imgUrl: newMealLog.imgUrl || ''
      };
      
      console.log('전송할 데이터:', mealLogData);
      
      if (isEditMode && newMealLog.logId) {
        await mealLogService.updateMealLog(newMealLog.logId, mealLogData);
      } else {
        await mealLogService.addMealLog(mealLogData);
      }
      
      handleClose();
      if (onMealLogAdded) {
        onMealLogAdded();
      }
    } catch (error) {
      console.error('식단 저장 실패:', error);
      if (error.response) {
        console.error('서버 응답:', error.response.data);
      }
      alert('식단 저장에 실패했습니다.');
    }
  };

  const formatDate = (dateString) => {
    try {
      return format(parseISO(dateString), 'yyyy년 MM월 dd일', { locale: ko });
    } catch (error) {
      console.error('날짜 형식 변환 오류:', error);
      return '날짜 정보 없음';
    }
  };

  const getMealLogsByType = (mealType) => {
    return mealLogs.filter(log => MEAL_TYPE_MAP[log.mealType] === mealType);
  };

  const renderMealSection = (mealType) => {
    const typeMealLogs = getMealLogsByType(mealType);

    return (
      <Box mb={3}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <Typography variant="h6">{mealType}</Typography>
          <Button 
            variant="contained" 
            color="primary" 
            size="small"
            onClick={() => handleOpen(mealType)}
          >
            추가하기
          </Button>
        </Box>
        {typeMealLogs.length > 0 ? (
          <List>
            {typeMealLogs.map((log, index) => (
              <React.Fragment key={log.id || index}>
                <ListItem sx={{ flexDirection: 'row', alignItems: 'flex-start', py: 2 }}>
                  <ListItemAvatar>
                    <Avatar
                      variant="rounded"
                      src={log.imgUrl || DEFAULT_FOOD_IMAGE}
                      alt={log.foodName}
                      sx={{ width: 80, height: 80, mr: 2 }}
                    />
                  </ListItemAvatar>
                  <ListItemText
                    primary={
                      <Box display="flex" alignItems="center" gap={1}>
                        <Typography variant="body1">
                          {log.foodName}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          ({log.quantity}g)
                        </Typography>
                      </Box>
                    }
                    secondary={
                      <Box mt={1}>
                        <Typography variant="body2" color="text.secondary">
                          칼로리: {log.calories}kcal | 단백질: {log.protein}g | 
                          탄수화물: {log.carbs}g | 지방: {log.fat}g
                        </Typography>
                      </Box>
                    }
                  />
                  <IconButton
                    onClick={(e) => handleMenuOpen(e, log)}
                    size="small"
                  >
                    <MoreVertIcon />
                  </IconButton>
                </ListItem>
                {index < typeMealLogs.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </List>
        ) : (
          <Typography variant="body2" color="text.secondary" align="center" py={2}>
            {mealType} 식단이 없습니다.
          </Typography>
        )}
      </Box>
    );
  };

  const nutrition = calculateNutrition();

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        {formatDate(selectedDate)} 식단
      </Typography>
      
      {MEAL_TYPES.map(mealType => renderMealSection(mealType))}

      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>{isEditMode ? '식단 수정' : '식단 추가'}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <Box display="flex" alignItems="center" gap={2}>
                <Avatar
                  src={newMealLog.imgUrl || DEFAULT_FOOD_IMAGE}
                  alt="음식 이미지"
                  sx={{ width: 100, height: 100 }}
                />
                <IconButton
                  color="primary"
                  onClick={handleImageClick}
                  disabled={uploadingImage}
                >
                  <PhotoCameraIcon />
                </IconButton>
                <input
                  type="file"
                  accept="image/*"
                  hidden
                  ref={fileInputRef}
                  onChange={handleImageUpload}
                />
              </Box>
            </Grid>
            <Grid item xs={12}>
              <Autocomplete
                options={foods}
                getOptionLabel={(option) => option.name}
                value={selectedFood}
                onChange={handleFoodSelect}
                onInputChange={(event, value) => setSearchTerm(value)}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    fullWidth
                    label="음식 검색"
                    placeholder="음식 이름을 입력하세요"
                    error={searchTerm.length > 0 && foods.length === 0}
                    helperText={searchTerm.length > 0 && foods.length === 0 ? "검색 결과가 없습니다." : ""}
                  />
                )}
                renderOption={(props, option) => (
                  <ListItem {...props}>
                    <ListItemAvatar>
                      <Avatar
                        src={option.imgUrl || DEFAULT_FOOD_IMAGE}
                        alt={option.name}
                      />
                    </ListItemAvatar>
                    <ListItemText
                      primary={option.name}
                      secondary={`칼로리: ${option.calories}kcal/100g`}
                    />
                  </ListItem>
                )}
                noOptionsText="검색 결과가 없습니다"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                type="number"
                label="수량 (g)"
                value={newMealLog.quantity}
                onChange={handleQuantityChange}
                InputProps={{
                  inputProps: { min: 0 }
                }}
              />
            </Grid>
            {nutrition && (
              <Grid item xs={12}>
                <TableContainer component={Paper} variant="outlined">
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>칼로리</TableCell>
                        <TableCell>탄수화물</TableCell>
                        <TableCell>당류</TableCell>
                        <TableCell>단백질</TableCell>
                        <TableCell>지방</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      <TableRow>
                        <TableCell>{nutrition.calories}kcal</TableCell>
                        <TableCell>{nutrition.carbs}g</TableCell>
                        <TableCell>{nutrition.sugar}g</TableCell>
                        <TableCell>{nutrition.protein}g</TableCell>
                        <TableCell>{nutrition.fat}g</TableCell>
                      </TableRow>
                    </TableBody>
                  </Table>
                </TableContainer>
              </Grid>
            )}
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>취소</Button>
          <Button 
            onClick={handleSubmit} 
            variant="contained" 
            color="primary"
            disabled={!selectedFood}
          >
            {isEditMode ? '수정' : '추가'}
          </Button>
        </DialogActions>
      </Dialog>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleEdit}>수정</MenuItem>
        <MenuItem onClick={handleDelete}>삭제</MenuItem>
      </Menu>
    </Box>
  );
};

export default RecentMeals; 