import React, { useState, useEffect } from "react";
import { Box, Container, Grid, Typography, TextField, MenuItem, Pagination, CircularProgress, Button, Dialog, DialogTitle, DialogContent, DialogActions } from "@mui/material";
import FoodCard from "../components/FoodCard";
import { foodService } from "../services/foodService";
import { foodRequestService } from '../services/FoodRequestService';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { styled } from '@mui/material/styles';

const categories = [
  "전체",
  "빵 및 과자류",
  "찜류",
  "구이류",
  "젓갈류",
  "생채·무침류",
  "나물·숙채류",
  "전·적 및 부침류",
  "면 및 만두류"
];

const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  clipPath: 'inset(50%)',
  height: 1,
  overflow: 'hidden',
  position: 'absolute',
  bottom: 0,
  left: 0,
  whiteSpace: 'nowrap',
  width: 1,
});

function Foods() {
  const [foods, setFoods] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState("전체");
  const [searchTerm, setSearchTerm] = useState("");
  const [searchInput, setSearchInput] = useState("");
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [openRequestModal, setOpenRequestModal] = useState(false);
  const [requestForm, setRequestForm] = useState({
    name: '',
    category: '',
    imgUrl: '',
    servingSize: '',
    unit: '',
    calories: '',
    carbs: '',
    sugar: '',
    protein: '',
    fat: ''
  });
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState('');

  useEffect(() => {
    const fetchFoods = async () => {
      try {
        setLoading(true);
        let response;
        
        if (searchTerm) {
          // 검색어가 있는 경우 검색 API 호출
          response = await foodService.searchFoods(searchTerm);
          console.log('검색 결과:', response);
          setFoods(response.content || []);
          setTotalPages(Math.ceil((response.totalElements || 0) / 10));
        } else {
          // 검색어가 없는 경우 전체 목록 조회
          response = await foodService.getFoods({
            category: selectedCategory === "전체" ? "" : selectedCategory,
            page: page - 1,
            size: 10
          });
          
          setFoods(response.content || []);
          setTotalPages(Math.ceil((response.totalElements || 0) / 10));
        }
      } catch (err) {
        setError("음식 목록을 불러오는데 실패했습니다.");
        console.error("음식 목록 조회 에러:", err);
        setFoods([]);
        setTotalPages(1);
      } finally {
        setLoading(false);
      }
    };

    fetchFoods();
  }, [selectedCategory, searchTerm, page]);

  const handleCategoryChange = (event) => {
    setSelectedCategory(event.target.value);
    setPage(1);
  };

  const handleSearchInputChange = (event) => {
    setSearchInput(event.target.value);
  };

  const handleSearch = () => {
    setSearchTerm(searchInput);
    setPage(1);
  };

  const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
      handleSearch();
    }
  };

  const handlePageChange = (event, value) => {
    setPage(value);
  };

  const handleOpenRequestModal = () => {
    setOpenRequestModal(true);
  };

  const handleCloseRequestModal = () => {
    setOpenRequestModal(false);
  };

  const handleRequestFormChange = (e) => {
    const { name, value } = e.target;
    setRequestForm(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      setSelectedFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result);
        setRequestForm(prev => ({
          ...prev,
          imgUrl: reader.result
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmitRequest = async () => {
    try {
      // JSON 데이터로 전송
      const requestData = {
        name: requestForm.name,
        category: requestForm.category,
        servingSize: parseFloat(requestForm.servingSize),
        unit: requestForm.unit,
        calories: parseFloat(requestForm.calories),
        carbs: parseFloat(requestForm.carbs),
        sugar: parseFloat(requestForm.sugar),
        protein: parseFloat(requestForm.protein),
        fat: parseFloat(requestForm.fat),
        imgUrl: requestForm.imgUrl
      };

      await foodRequestService.createFoodRequest(requestData);
      handleCloseRequestModal();
      setSelectedFile(null);
      setPreviewUrl('');
      alert('음식 추가 요청이 완료되었습니다.');
    } catch (error) {
      console.error('음식 추가 요청 실패:', error);
      alert('음식 추가 요청에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ background: "#eaf6ff", minHeight: "100vh", py: 6 }}>
      <Container maxWidth="lg">
        <Typography 
          variant="h4" 
          sx={{ 
            color: "#ff4d29", 
            fontWeight: 700, 
            textAlign: "center", 
            mb: 5,
            textShadow: "2px 2px 4px rgba(0,0,0,0.1)"
          }}
        >
          음식 목록
        </Typography>

        <Box sx={{ mb: 4, display: "flex", gap: 2, flexWrap: "wrap" }}>
          <TextField
            select
            label="카테고리"
            value={selectedCategory}
            onChange={handleCategoryChange}
            sx={{ minWidth: 200 }}
          >
            {categories.map((category) => (
              <MenuItem key={category} value={category}>
                {category}
              </MenuItem>
            ))}
          </TextField>

          <Box sx={{ display: 'flex', flexGrow: 1, gap: 1 }}>
            <TextField
              label="음식 검색"
              value={searchInput}
              onChange={handleSearchInputChange}
              onKeyPress={handleKeyPress}
              sx={{ flexGrow: 1 }}
              placeholder="음식 이름을 입력하고 엔터를 누르세요"
            />
            <Button
              variant="contained"
              onClick={handleSearch}
              sx={{
                background: 'linear-gradient(135deg, #FF6B6B, #FF8E53)',
                color: 'white',
                '&:hover': {
                  background: 'linear-gradient(135deg, #FF8E53, #FF6B6B)',
                },
              }}
            >
              검색
            </Button>
          </Box>
        </Box>

        {foods && foods.length > 0 ? (
          <>
            <Box
              sx={{
                display: 'grid',
                gridTemplateColumns: 'repeat(4, 1fr)',
                gap: 2,
                justifyItems: 'start',
                alignItems: 'stretch',
                width: '100%',
                pl: 2,
                mb: 4,
              }}
            >
              {foods.slice(0, 8).map((food) => (
                <FoodCard key={food.foodId} food={food} />
              ))}
            </Box>

            <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={handlePageChange}
                color="primary"
                size="large"
                sx={{
                  '& .MuiPaginationItem-root': {
                    fontSize: '1.1rem',
                    fontWeight: 600,
                  },
                  '& .Mui-selected': {
                    backgroundColor: '#ff4d29 !important',
                    color: 'white !important',
                  },
                }}
              />
            </Box>
          </>
        ) : (
          <Box sx={{ textAlign: "center", py: 4 }}>
            <Typography variant="h6" color="text.secondary">
              검색 결과가 없습니다.
            </Typography>
          </Box>
        )}

        <Button 
          variant="contained" 
          color="primary" 
          onClick={handleOpenRequestModal}
          sx={{ mb: 2 }}
        >
          음식 추가 요청
        </Button>

        <Dialog open={openRequestModal} onClose={handleCloseRequestModal} maxWidth="md" fullWidth>
          <DialogTitle>음식 추가 요청</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="음식 이름"
                  name="name"
                  value={requestForm.name}
                  onChange={handleRequestFormChange}
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="카테고리"
                  name="category"
                  value={requestForm.category}
                  onChange={handleRequestFormChange}
                  select
                >
                  <MenuItem value="KOREAN">한식</MenuItem>
                  <MenuItem value="CHINESE">중식</MenuItem>
                  <MenuItem value="JAPANESE">일식</MenuItem>
                  <MenuItem value="WESTERN">양식</MenuItem>
                  <MenuItem value="SNACK">간식</MenuItem>
                </TextField>
              </Grid>
              <Grid item xs={12}>
                <Button
                  component="label"
                  variant="outlined"
                  startIcon={<CloudUploadIcon />}
                  sx={{ width: '100%', mb: 2 }}
                >
                  이미지 업로드
                  <VisuallyHiddenInput type="file" accept="image/*" onChange={handleFileChange} />
                </Button>
                {previewUrl && (
                  <Box sx={{ mt: 2, textAlign: 'center' }}>
                    <Typography variant="subtitle2" gutterBottom>
                      이미지 미리보기
                    </Typography>
                    <Box
                      component="img"
                      src={previewUrl}
                      alt="음식 이미지 미리보기"
                      sx={{
                        maxWidth: '100%',
                        maxHeight: '200px',
                        objectFit: 'contain',
                        border: '1px solid #ddd',
                        borderRadius: '4px',
                        p: 1
                      }}
                    />
                  </Box>
                )}
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="1회 제공량"
                  name="servingSize"
                  type="number"
                  value={requestForm.servingSize}
                  onChange={handleRequestFormChange}
                  required
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="단위"
                  name="unit"
                  value={requestForm.unit}
                  onChange={handleRequestFormChange}
                  required
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="칼로리"
                  name="calories"
                  type="number"
                  value={requestForm.calories}
                  onChange={handleRequestFormChange}
                  required
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="탄수화물"
                  name="carbs"
                  type="number"
                  value={requestForm.carbs}
                  onChange={handleRequestFormChange}
                  required
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="당류"
                  name="sugar"
                  type="number"
                  value={requestForm.sugar}
                  onChange={handleRequestFormChange}
                  required
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="단백질"
                  name="protein"
                  type="number"
                  value={requestForm.protein}
                  onChange={handleRequestFormChange}
                  required
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="지방"
                  name="fat"
                  type="number"
                  value={requestForm.fat}
                  onChange={handleRequestFormChange}
                  required
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseRequestModal}>취소</Button>
            <Button onClick={handleSubmitRequest} variant="contained" color="primary">
              요청하기
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
}

export default Foods;