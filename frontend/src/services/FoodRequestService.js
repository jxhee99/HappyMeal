import axiosInstance from './axiosConfig';

export const foodRequestService = {
    // 음식 추가 요청 생성
    createFoodRequest: async (foodRequestData) => {
        const response = await axiosInstance.post('/food-requests', foodRequestData);
        return response.data;
    },

    // 모든 음식 요청 조회 (관리자용)
    getAllFoodRequests: async () => {
        const response = await axiosInstance.get('/food-requests');
        return response.data;
    },

    // 내 음식 요청 조회
    getUserFoodRequests: async () => {
        const response = await axiosInstance.get('/food-requests/user');
        return response.data;
    },

    // 음식 요청 상태 업데이트 (관리자용)
    updateFoodRequestStatus: async (requestId, status) => {
        const response = await axiosInstance.patch(`/food-requests/${requestId}/status`, { isRegistered: status });
        return response.data;
    }
}; 