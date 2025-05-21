import axios from 'axios';
import axiosInstance from './axiosConfig';

const API_BASE_URL = 'http://localhost:8080/api';

export const foodService = {
  getAllFoods: async (params) => {
    const response = await axios.get('/foods', { params });
    return response.data;
  },

  searchFoodsByName: async (name, params) => {
    const response = await axios.get('/foods/search', {
      params: { name, ...params }
    });
    return response.data;
  },

  getFoodById: async (foodId) => {
    try {
      const response = await axiosInstance.get(`/foods/${foodId}`);
      return response.data;
    } catch (error) {
      console.error('음식 상세 조회 실패:', error);
      throw error;
    }
  },

  updateFood: async (foodId, foodData) => {
    try {
      const response = await axios.put(`${API_BASE_URL}/foods/${foodId}`, foodData);
      return response.data;
    } catch (error) {
      console.error('음식 정보 수정 실패:', error);
      throw error;
    }
  },

  deleteFood: async (foodId) => {
    try {
      const response = await axios.delete(`${API_BASE_URL}/foods/${foodId}`);
      return response.data;
    } catch (error) {
      console.error('음식 삭제 실패:', error);
      throw error;
    }
  },

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

  searchFoods: async (searchTerm) => {
    try {
      const response = await axiosInstance.get('/foods/search', {
        params: { 
          name: searchTerm,
          sortBy: 'name ASC',
          page: 0,
          size: 10
        }
      });
      return response.data.content;
    } catch (error) {
      console.error('음식 검색 실패:', error);
      throw error;
    }
  }
};