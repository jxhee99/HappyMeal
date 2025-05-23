import axiosInstance from './axiosConfig';

export const foodRequestService = {
    // 음식 추가 요청 생성
    createFoodRequest: async (foodRequestData) => {
        try {
            const response = await axiosInstance.post('/food-requests', foodRequestData);
            return response.data;
        } catch (error) {
            console.error('음식 요청 생성 실패:', error);
            throw error;
        }
    },

    // 모든 음식 요청 조회 (관리자용)
    getAllFoodRequests: async () => {
        try {
            const response = await axiosInstance.get('/food-requests');
            return response.data;
        } catch (error) {
            console.error('음식 요청 목록 조회 실패:', error);
            if (error.response?.status === 403) {
                throw new Error('관리자 권한이 필요합니다.');
            }
            throw error;
        }
    },

    // 내 음식 요청 조회
    getUserFoodRequests: async () => {
        try {
            const response = await axiosInstance.get('/food-requests/user');
            return response.data;
        } catch (error) {
            console.error('내 음식 요청 목록 조회 실패:', error);
            throw error;
        }
    },

    // 음식 요청 상태 업데이트 (관리자용)
    updateFoodRequestStatus: async (requestId, status) => {
        try {
            const response = await axiosInstance.patch(`/food-requests/${requestId}/status`, { isRegistered: status });
            return response.data;
        } catch (error) {
            console.error('음식 요청 상태 업데이트 실패:', error);
            if (error.response?.status === 403) {
                throw new Error('관리자 권한이 필요합니다.');
            }
            throw error;
        }
    }
}; 