import React, { useState, useEffect } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  TextField,
  IconButton,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Add as AddIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import BoardService from '../services/BoardService';

const BoardEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [post, setPost] = useState(null);
  const [title, setTitle] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [blocks, setBlocks] = useState([]);

  useEffect(() => {
    const fetchBoard = async () => {
      try {
        console.log('게시글 수정 페이지 로드:', { id });
        setLoading(true);
        const response = await BoardService.getBoardDetail(id);
        console.log('게시글 데이터 로드 성공:', response.data);
        const boardData = response.data;
        setPost(boardData);
        setTitle(boardData.title);
        setCategoryId(boardData.categoryId);
        setBlocks(boardData.blocks || []);
      } catch (error) {
        console.error('게시글 로딩 실패:', error);
        setError('게시글을 불러오는데 실패했습니다.');
        setTimeout(() => {
          navigate(`/board/${id}`);
        }, 2000);
      } finally {
        setLoading(false);
      }
    };

    if (!id) {
      setError('게시글 ID가 없습니다.');
      return;
    }

    fetchBoard();
  }, [id, navigate]);

  const handleAddBlock = () => {
    setBlocks([...blocks, {
      blockType: 'text',
      contentText: '',
      orderIndex: blocks.length,
    }]);
  };

  const handleDeleteBlock = (index) => {
    const newBlocks = blocks.filter((_, i) => i !== index);
    setBlocks(newBlocks.map((block, i) => ({ ...block, orderIndex: i })));
  };

  const handleBlockChange = (index, field, value) => {
    const newBlocks = [...blocks];
    newBlocks[index] = { ...newBlocks[index], [field]: value };
    setBlocks(newBlocks);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim()) {
      setError('제목을 입력해주세요.');
      return;
    }

    try {
      const updatedBlocks = blocks.map((block, index) => ({
        blockId: block.blockId || null,
        orderIndex: index,
        blockType: block.blockType,
        contentText: block.contentText || '',
        imageUrl: block.imageUrl || '',
        imageCaption: block.imageCaption || ''
      }));

      const updateData = {
        title,
        categoryId: Number(categoryId),
        blocks: updatedBlocks
      };

      console.log('게시글 수정 요청 데이터:', updateData);

      await BoardService.updateBoard(id, updateData);
      navigate(`/board/${id}`);
    } catch (error) {
      console.error('게시글 수정 실패:', error);
      if (error.response) {
        console.error('서버 응답:', error.response.data);
        setError(`게시글 수정에 실패했습니다: ${error.response.data.message || '알 수 없는 오류가 발생했습니다.'}`);
      } else {
        setError('게시글 수정에 실패했습니다.');
      }
    }
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate(`/board/${id}`)}
          sx={{ mt: 2 }}
        >
          돌아가기
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Paper sx={{ p: 3, borderRadius: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
          <IconButton onClick={() => navigate(`/board/${id}`)} sx={{ mr: 2 }}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h5" component="h1">
            게시글 수정
          </Typography>
        </Box>

        <Box component="form" onSubmit={handleSubmit}>
          <TextField
            fullWidth
            label="제목"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            sx={{ mb: 3 }}
            required
          />

          <TextField
            fullWidth
            label="카테고리 ID"
            type="number"
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
            sx={{ mb: 3 }}
            required
          />

          {blocks.map((block, index) => (
            <Box key={index} sx={{ mb: 3, p: 2, border: '1px solid #ddd', borderRadius: 1 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="subtitle1">블록 {index + 1}</Typography>
                <IconButton onClick={() => handleDeleteBlock(index)} color="error">
                  <DeleteIcon />
                </IconButton>
              </Box>

              <TextField
                select
                fullWidth
                label="블록 타입"
                value={block.blockType}
                onChange={(e) => handleBlockChange(index, 'blockType', e.target.value)}
                sx={{ mb: 2 }}
                SelectProps={{
                  native: true,
                }}
              >
                <option value="text">텍스트</option>
                <option value="image">이미지</option>
              </TextField>

              {block.blockType === 'text' ? (
                <TextField
                  fullWidth
                  multiline
                  rows={4}
                  label="내용"
                  value={block.contentText}
                  onChange={(e) => handleBlockChange(index, 'contentText', e.target.value)}
                />
              ) : (
                <>
                  <TextField
                    fullWidth
                    label="이미지 URL"
                    value={block.imageUrl}
                    onChange={(e) => handleBlockChange(index, 'imageUrl', e.target.value)}
                    sx={{ mb: 2 }}
                  />
                  <TextField
                    fullWidth
                    label="이미지 설명"
                    value={block.imageCaption}
                    onChange={(e) => handleBlockChange(index, 'imageCaption', e.target.value)}
                  />
                </>
              )}
            </Box>
          ))}

          <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
            <Button
              variant="outlined"
              startIcon={<AddIcon />}
              onClick={handleAddBlock}
            >
              블록 추가
            </Button>
          </Box>

          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
            <Button
              variant="outlined"
              onClick={() => navigate(`/board/${id}`)}
            >
              취소
            </Button>
            <Button
              type="submit"
              variant="contained"
            >
              수정하기
            </Button>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
};

export default BoardEdit; 