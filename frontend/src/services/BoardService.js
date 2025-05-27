import axiosInstance from './axiosConfig';

const BoardService = {
  // 게시글 목록 조회
  getBoards: async (params) => {
    try {
      console.log('게시글 목록 조회 파라미터:', params); // 디버깅용 로그
      const response = await axiosInstance.get('/boards', {
        params: {
          page: params.page || 0,
          size: params.size || 10,
          sortBy: params.sortBy || 'latest',
          categoryId: params.categoryId || null
        }
      });
      console.log('게시글 목록 응답:', response.data); // 디버깅용 로그
      return response;
    } catch (error) {
      console.error('게시글 목록 조회 API 호출 실패:', error);
      if (error.response) {
        // 서버에서 응답이 왔지만 에러인 경우
        console.error('서버 응답:', error.response.data);
        console.error('상태 코드:', error.response.status);
        console.error('헤더:', error.response.headers);
      } else if (error.request) {
        // 요청은 보냈지만 응답이 없는 경우
        console.error('서버 응답 없음:', error.request);
      } else {
        // 요청 설정 중 에러가 발생한 경우
        console.error('에러 메시지:', error.message);
      }
      throw error;
    }
  },

  // 제목으로 검색
  searchByTitle: async (title, params) => {
    try {
      const response = await axiosInstance.get('/boards/search/title', {
        params: {
          title,
          page: params.page || 0,
          size: params.size || 10
        }
      });
      return response;
    } catch (error) {
      console.error('제목 검색 API 호출 실패:', error);
      throw error;
    }
  },

  // 작성자로 검색
  searchByAuthor: async (author, params) => {
    try {
      const response = await axiosInstance.get('/boards/search/author', {
        params: {
          nickname: author,
          page: params.page || 0,
          size: params.size || 10
        }
      });
      return response;
    } catch (error) {
      console.error('작성자 검색 API 호출 실패:', error);
      throw error;
    }
  },

  // 게시글 상세 조회
  getBoardDetail: async (boardId) => {
    try {
      const response = await axiosInstance.get(`/boards/${boardId}`);
      return response;
    } catch (error) {
      console.error('게시글 상세 조회 API 호출 실패:', error);
      throw error;
    }
  },

  // 게시글 작성
  createBoard: async (boardData) => {
    try {
      const response = await axiosInstance.post('/boards', boardData);
      return response;
    } catch (error) {
      console.error('게시글 작성 API 호출 실패:', error);
      throw error;
    }
  },

  // 게시글 수정
  updateBoard: async (boardId, boardData) => {
    try {
      const response = await axiosInstance.put(`/boards/${boardId}`, boardData);
      return response;
    } catch (error) {
      console.error('게시글 수정 API 호출 실패:', error);
      throw error;
    }
  },

  // 게시글 삭제
  deleteBoard: async (boardId) => {
    try {
      const response = await axiosInstance.delete(`/boards/${boardId}`);
      return response;
    } catch (error) {
      console.error('게시글 삭제 API 호출 실패:', error);
      throw error;
    }
  },

  // 좋아요 토글
  toggleLike: async (boardId) => {
    try {
      const response = await axiosInstance.post(`/boards/${boardId}/like`);
      return response;
    } catch (error) {
      console.error('좋아요 토글 API 호출 실패:', error);
      throw error;
    }
  },

  // 좋아요 상태 조회
  getLikeStatus: async (boardId) => {
    try {
      const response = await axiosInstance.get(`/boards/${boardId}/like`);
      return response;
    } catch (error) {
      console.error('좋아요 상태 조회 API 호출 실패:', error);
      throw error;
    }
  },

  // 사용자가 좋아요한 게시글 목록 조회
  getLikedBoards: async (page = 0, size = 10) => {
    try {
      const response = await axiosInstance.get(`/boards/liked?page=${page}&size=${size}`);
      return response;
    } catch (error) {
      console.error('사용자가 좋아요한 게시글 목록 조회 API 호출 실패:', error);
      throw error;
    }
  },

  // 댓글 작성
  createComment: async (boardId, commentData) => {
    try {
      const response = await axiosInstance.post(`/boards/${boardId}/comments`, commentData);
      return response;
    } catch (error) {
      console.error('댓글 작성 API 호출 실패:', error);
      throw error;
    }
  },

  // 댓글 목록 조회
  getBoardComments: async (boardId) => {
    try {
      const response = await axiosInstance.get(`/boards/${boardId}/comments`);
      return response;
    } catch (error) {
      console.error('댓글 목록 조회 API 호출 실패:', error);
      throw error;
    }
  },

  // 댓글 삭제
  deleteComment: async (boardId, commentId) => {
    try {
      const response = await axiosInstance.delete(`/comments/${boardId}/${commentId}`);
      return response;
    } catch (error) {
      console.error('댓글 삭제 API 호출 실패:', error);
      throw error;
    }
  }
};

export default BoardService; 