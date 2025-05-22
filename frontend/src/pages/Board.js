import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Button,
  TextField,
  MenuItem,
  TablePagination,
  Chip,
  IconButton,
  Tooltip,
  InputAdornment,
  Tabs,
  Tab,
  Alert,
  CircularProgress,
  Pagination,
} from '@mui/material';
import {
  Search as SearchIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import BoardService from '../services/BoardService';
import BoardCard from '../components/BoardCard';

const categories = [
  { id: 1, name: '맛집 추천 및 리뷰' },
  { id: 2, name: '레시피 및 식단 공유' },
  { id: 3, name: '자유게시판' },
  { id: 4, name: '공지' },
];

const Board = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [posts, setPosts] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchType, setSearchType] = useState('title'); // 'title' 또는 'author'
  const [error, setError] = useState('');
  const [totalPages, setTotalPages] = useState(0);

  const fetchPosts = async () => {
    try {
      setLoading(true);
      setError('');
      const params = {
        page,
        size: rowsPerPage,
        sortBy: 'latest',
        ...(selectedCategory ? { categoryId: selectedCategory } : {})
      };
      const response = await BoardService.getBoards(params);
      if (response.data && Array.isArray(response.data.content)) {
        setPosts(response.data.content || []);
        setTotalElements(response.data.totalElements || 0);
        setTotalPages(response.data.totalPages || 0);
      } else {
        setError('서버에서 잘못된 응답을 받았습니다.');
        setPosts([]);
        setTotalElements(0);
        setTotalPages(0);
      }
    } catch (error) {
      if (error.response) {
        const errorMessage = error.response.data?.message || '알 수 없는 오류가 발생했습니다.';
        setError(`게시글 목록을 불러오는데 실패했습니다. (${error.response.status}): ${errorMessage}`);
      } else if (error.request) {
        setError('서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      } else {
        setError('게시글 목록을 불러오는데 실패했습니다.');
      }
      setPosts([]);
      setTotalElements(0);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, [page, rowsPerPage, selectedCategory]);

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchPosts();
      return;
    }
    try {
      setLoading(true);
      setError('');
      const response = searchType === 'title'
        ? await BoardService.searchByTitle(searchTerm, { page, size: rowsPerPage })
        : await BoardService.searchByAuthor(searchTerm, { page, size: rowsPerPage });
      
      if (response.data && Array.isArray(response.data.content)) {
        setPosts(response.data.content || []);
        setTotalElements(response.data.totalElements || 0);
        setTotalPages(response.data.totalPages || 0);
      } else {
        setError(`'${searchTerm}'에 대한 검색 결과가 없습니다.`);
        setPosts([]);
        setTotalElements(0);
        setTotalPages(0);
      }
    } catch (error) {
      if (error.response) {
        const errorMessage = error.response.data?.message || '알 수 없는 오류가 발생했습니다.';
        setError(`게시글 검색에 실패했습니다. (${error.response.status}): ${errorMessage}`);
      } else if (error.request) {
        setError('서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
      } else {
        setError('게시글 검색에 실패했습니다.');
      }
      setPosts([]);
      setTotalElements(0);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleSearchTypeChange = (event, newValue) => {
    setSearchType(newValue);
    setSearchTerm('');
    fetchPosts();
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Paper sx={{ p: 3, borderRadius: 2 }}>
            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            {/* 검색 및 필터 */}
            <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
              <Box sx={{ display: 'flex', gap: 1, flexGrow: 1 }}>
                <TextField
                  select
                  value={searchType}
                  onChange={(e) => setSearchType(e.target.value)}
                  sx={{ width: 120 }}
                  size="small"
                >
                  <MenuItem value="title">제목</MenuItem>
                  <MenuItem value="author">작성자</MenuItem>
                </TextField>
                <TextField
                  fullWidth
                  variant="outlined"
                  placeholder={searchType === 'title' ? "제목으로 검색" : "작성자로 검색"}
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <SearchIcon />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton onClick={handleSearch}>
                          <SearchIcon />
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </Box>
              <Button
                variant="contained"
                color="primary"
                startIcon={<AddIcon />}
                onClick={() => navigate('/board/write')}
              >
                글쓰기
              </Button>
            </Box>

            {/* 카테고리 탭 */}
            <Tabs
              value={selectedCategory}
              onChange={(e, newValue) => setSelectedCategory(newValue)}
              sx={{ mb: 3 }}
              variant="scrollable"
              scrollButtons="auto"
            >
              <Tab value="" label="전체" />
              {categories.map((category) => (
                <Tab
                  key={category.id}
                  value={category.id}
                  label={category.name}
                />
              ))}
            </Tabs>

            {/* 게시글 목록 */}
            {loading ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                <CircularProgress />
              </Box>
            ) : (
              <Grid container spacing={2}>
                {posts?.length > 0 ? (
                  posts.map((post) => (
                    <Grid item xs={12} key={post.boardId}>
                      <BoardCard
                        post={post}
                        categories={categories}
                        onClick={() => navigate(`/board/${post.boardId}`)}
                      />
                    </Grid>
                  ))
                ) : (
                  <Grid item xs={12}>
                    <Box sx={{ textAlign: 'center', py: 4 }}>
                      <Typography variant="body1" color="text.secondary">
                        {searchTerm ? `'${searchTerm}'에 대한 검색 결과가 없습니다.` : '게시글이 없습니다.'}
                      </Typography>
                    </Box>
                  </Grid>
                )}
              </Grid>
            )}

            {/* 페이지네이션 */}
            <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
              <Pagination
                count={totalPages}
                page={page + 1}
                onChange={(event, value) => handleChangePage(event, value - 1)}
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
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Board; 