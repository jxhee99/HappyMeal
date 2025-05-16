import React, { useState, useEffect } from "react";
import { Box, Container, Grid, Typography, TextField, MenuItem, Pagination, CircularProgress, Button } from "@mui/material";
import FoodCard from "../components/FoodCard";
import { foodService } from "../services/foodService";

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

function Foods() {
  const [foods, setFoods] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState("전체");
  const [searchTerm, setSearchTerm] = useState("");
  const [searchInput, setSearchInput] = useState("");
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    const fetchFoods = async () => {
      try {
        setLoading(true);
        let response;
        
        if (searchTerm) {
          // 검색어가 있는 경우 검색 API 호출
          response = await foodService.searchFoods(searchTerm);
          setFoods(response || []);
          setTotalPages(1); // 검색 결과는 페이지네이션 없이 한 페이지에 모두 표시
        } else {
          // 검색어가 없는 경우 전체 목록 조회
          response = await foodService.getFoods({
            category: selectedCategory === "전체" ? "" : selectedCategory,
            search: searchTerm
          });
          
          // 응답이 배열인 경우 직접 사용
          const foodsList = Array.isArray(response) ? response : response.content || [];
          
          // 카테고리로 필터링
          let filteredFoods = foodsList;
          if (selectedCategory !== "전체") {
            filteredFoods = filteredFoods.filter(food => food.category === selectedCategory);
          }
          
          // 페이지네이션 처리
          const startIndex = (page - 1) * 10;
          const endIndex = startIndex + 10;
          const paginatedFoods = filteredFoods.slice(startIndex, endIndex);
          
          setFoods(paginatedFoods);
          setTotalPages(Math.ceil(filteredFoods.length / 10));
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
            <Grid container spacing={3} justifyContent="center">
              {foods.map((food) => (
                <Grid item key={food.foodId}>
                  <FoodCard food={food} />
                </Grid>
              ))}
            </Grid>

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
      </Container>
    </Box>
  );
}

export default Foods;