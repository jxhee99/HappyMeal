import axiosInstance from './axiosConfig';

export const mealLogService = {
  // 식단 기록 추가
  addMealLog: async (mealLogData) => {
    try {
      const response = await axiosInstance.post('/meallogs', mealLogData);
      return response.data;
    } catch (error) {
      console.error('식단 기록 추가 실패:', error);
      throw error;
    }
  },

  // 전체 식단 기록 조회
  getAllMealLogs: async () => {
    try {
      const response = await axiosInstance.get('/meallogs');
      return response.data;
    } catch (error) {
      console.error('전체 식단 기록 조회 실패:', error);
      throw error;
    }
  },

  // 특정 날짜 식단 기록 조회
  getMealLogsByDate: async (date) => {
    try {
      const response = await axiosInstance.get('/meallogs', {
        params: { date }
      });
      return response.data;
    } catch (error) {
      console.error('특정 날짜 식단 기록 조회 실패:', error);
      throw error;
    }
  },

  // 특정 날짜 식단 통계 조회
  getMealLogStatsByDate: async (date) => {
    try {
      const response = await axiosInstance.get('/meallogs/stats', {
        params: { date }
      });
      return response.data;
    } catch (error) {
      console.error('특정 날짜 식단 통계 조회 실패:', error);
      throw error;
    }
  },

  // 식단 기록 상세 조회
  getDetailMealLog: async (logId) => {
    try {
      const response = await axiosInstance.get(`/meallogs/${logId}`);
      return response.data;
    } catch (error) {
      console.error('식단 기록 상세 조회 실패:', error);
      throw error;
    }
  },

  // 식단 기록 삭제
  deleteMealLog: async (logId) => {
    try {
      const response = await axiosInstance.delete(`/meallogs/${logId}`);
      return response.data;
    } catch (error) {
      console.error('식단 기록 삭제 실패:', error);
      throw error;
    }
  },

  // 최근 식단 기록 조회 (전체 조회 후 최근 5개만 반환)
  getRecentMealLogs: async () => {
    try {
      const response = await axiosInstance.get('/meallogs');
      return response.data.slice(0, 5); // 최근 5개만 반환
    } catch (error) {
      console.error('최근 식단 기록 조회 실패:', error);
      throw error;
    }
  },

  // 오늘의 영양소 요약 조회
  getTodayNutritionSummary: async () => {
    try {
      const today = new Date().toISOString().split('T')[0];
      const response = await axiosInstance.get('/meallogs/stats', {
        params: { date: today }
      });
      return response.data;
    } catch (error) {
      console.error('오늘의 영양소 요약 조회 실패:', error);
      throw error;
    }
  },

  // 주간 식단 기록 조회
  getWeeklyMealLogs: async () => {
    try {
      const today = new Date();
      const promises = [];
      
      // 현재 날짜부터 6일 전까지의 날짜 배열 생성
      for (let i = 6; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(today.getDate() - i);
        const dateStr = date.toISOString().split('T')[0];
        
        promises.push(
          axiosInstance.get('/meallogs/stats', {
            params: { date: dateStr }
          }).then(response => ({
            date: dateStr,
            ...response.data
          })).catch(error => {
            // 통계 데이터가 없는 경우 0으로 채우기
            console.log(`${dateStr} 통계 데이터 없음`);
            return {
              date: dateStr,
              calories: 0,
              protein: 0,
              carbs: 0,
              fat: 0
            };
          })
        );
      }

      const responses = await Promise.all(promises);
      return responses;
    } catch (error) {
      console.error('주간 식단 기록 조회 실패:', error);
      throw error;
    }
  }
}; 