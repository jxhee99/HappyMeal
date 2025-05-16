import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const foodService = {
  getRecommendedFoods: async (category) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/foods/recommendations`, {
        params: { category }
      });
      return response.data;
    } catch (error) {
      console.error('추천 음식 조회 실패:', error);
      throw error;
    }
  },

  getFoods: async ({ category = '', search = '' }) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/foods`);
      console.log('음식 목록 응답:', response.data);
      return response.data;
    } catch (error) {
      console.error('음식 목록 조회 실패:', error);
      throw error;
    }
  },

  searchFoods: async (name) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/foods/search`, {
        params: { name }
      });
      console.log('음식 검색 응답:', response.data);
      return response.data;
    } catch (error) {
      console.error('음식 검색 실패:', error);
      throw error;
    }
  }
};