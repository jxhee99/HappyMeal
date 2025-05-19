import React, { useState } from 'react';
import {
  Container,
  Grid,
  Paper,
  Typography,
  Box,
  Button,
  TextField,
  MenuItem,
  Alert,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Image as ImageIcon,
  FormatBold,
  FormatItalic,
  FormatUnderlined,
  FormatListBulleted,
  FormatListNumbered,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import BoardService from '../services/BoardService';

const categories = [
  { id: 1, name: '맛집 추천 및 리뷰' },
  { id: 2, name: '레시피 및 식단 공유' },
  { id: 3, name: '자유게시판' },
  { id: 4, name: '공지' },
];

const BoardWrite = () => {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  // 이미지 마크다운을 기준으로 블록을 분리하는 함수
  const parseBlocks = (content) => {
    const blocks = [];
    const regex = /!\[(.*?)\]\((.*?)\)/g;
    let lastIndex = 0;
    let match;
    let orderIndex = 0;

    while ((match = regex.exec(content)) !== null) {
      // 이미지 앞의 텍스트 블록
      if (match.index > lastIndex) {
        const text = content.slice(lastIndex, match.index).trim();
        if (text) {
          blocks.push({
            orderIndex: orderIndex++,
            blockType: 'text',
            contentText: text,
            imageUrl: null,
            imageCaption: null
          });
        }
      }
      // 이미지 블록
      blocks.push({
        orderIndex: orderIndex++,
        blockType: 'image',
        contentText: null,
        imageUrl: match[2],
        imageCaption: match[1]
      });
      lastIndex = regex.lastIndex;
    }
    // 마지막 텍스트 블록
    if (lastIndex < content.length) {
      const text = content.slice(lastIndex).trim();
      if (text) {
        blocks.push({
          orderIndex: orderIndex++,
          blockType: 'text',
          contentText: text,
          imageUrl: null,
          imageCaption: null
        });
      }
    }
    return blocks;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!title.trim()) {
      setError('제목을 입력해주세요.');
      return;
    }

    if (!categoryId) {
      setError('카테고리를 선택해주세요.');
      return;
    }

    if (!content.trim()) {
      setError('내용을 입력해주세요.');
      return;
    }

    try {
      // 이미지 마크다운을 기준으로 블록 분리
      const blocks = parseBlocks(content);

      const response = await BoardService.createBoard({
        title,
        categoryId,
        blocks
      });

      navigate(`/board/${response.data.boardId}`);
    } catch (error) {
      console.error('게시글 작성 실패:', error);
      setError('게시글 작성에 실패했습니다. 다시 시도해주세요.');
    }
  };

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      // TODO: 이미지 업로드 API 연동
      // const formData = new FormData();
      // formData.append('image', file);
      // const response = await api.post('/upload', formData);
      // const imageUrl = response.data.url;
      
      // 임시로 이미지 URL 생성 (실제로는 서버에서 받아와야 함)
      const imageUrl = URL.createObjectURL(file);
      
      // 이미지 블록 생성
      const imageBlock = {
        orderIndex: content.split('\n\n').length,
        blockType: 'image',
        contentText: null,
        imageUrl: imageUrl,
        imageCaption: '업로드된 이미지'
      };

      // 현재 내용을 블록으로 분할
      const blocks = content.split('\n\n').map((text, index) => ({
        orderIndex: index,
        blockType: 'text',
        contentText: text.trim(),
        imageUrl: null,
        imageCaption: null
      }));

      // 이미지 블록 추가
      blocks.push(imageBlock);

      // 블록들을 다시 텍스트로 변환
      const newContent = blocks.map(block => {
        if (block.blockType === 'text') {
          return block.contentText;
        } else if (block.blockType === 'image') {
          return `\n\n![${block.imageCaption}](${block.imageUrl})\n\n`;
        }
        return '';
      }).join('\n\n');

      setContent(newContent);
    } catch (error) {
      console.error('이미지 업로드 실패:', error);
      setError('이미지 업로드에 실패했습니다.');
    }
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/board')}
        sx={{ mb: 2 }}
      >
        목록으로
      </Button>

      <Paper sx={{ p: 3, borderRadius: 2 }}>
        <Typography variant="h5" gutterBottom>
          게시글 작성
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="제목"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                sx={{ mb: 2 }}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                select
                required
                fullWidth
                label="카테고리"
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                sx={{ mb: 2 }}
              >
                {categories.map((cat) => (
                  <MenuItem key={cat.id} value={cat.id}>
                    {cat.name}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>

            <Grid item xs={12}>
              <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
                <Box sx={{ display: 'flex', gap: 1, mb: 2, borderBottom: 1, borderColor: 'divider', pb: 1 }}>
                  <Tooltip title="굵게">
                    <IconButton size="small">
                      <FormatBold />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="기울임">
                    <IconButton size="small">
                      <FormatItalic />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="밑줄">
                    <IconButton size="small">
                      <FormatUnderlined />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="글머리 기호">
                    <IconButton size="small">
                      <FormatListBulleted />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="번호 매기기">
                    <IconButton size="small">
                      <FormatListNumbered />
                    </IconButton>
                  </Tooltip>
                  <Box sx={{ flexGrow: 1 }} />
                  <Tooltip title="이미지 삽입">
                    <IconButton
                      component="label"
                      size="small"
                    >
                      <ImageIcon />
                      <input
                        type="file"
                        hidden
                        accept="image/*"
                        onChange={handleImageUpload}
                      />
                    </IconButton>
                  </Tooltip>
                </Box>
                <TextField
                  fullWidth
                  multiline
                  minRows={15}
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  placeholder="내용을 입력하세요. 빈 줄을 입력하면 새로운 단락이 시작됩니다."
                  variant="standard"
                  InputProps={{
                    disableUnderline: true,
                  }}
                  sx={{
                    '& .MuiInputBase-root': {
                      fontSize: '1.1rem',
                      lineHeight: 1.8,
                    },
                  }}
                />
              </Paper>
            </Grid>

            <Grid item xs={12}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/board')}
                >
                  취소
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  color="primary"
                >
                  작성하기
                </Button>
              </Box>
            </Grid>
          </Grid>
        </Box>
      </Paper>
    </Container>
  );
};

export default BoardWrite; 