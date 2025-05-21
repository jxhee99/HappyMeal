import axiosInstance from './axiosConfig';

export const imageService = {
  uploadImage: async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await axiosInstance.post('/images/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      console.error('이미지 업로드 실패:', error);
      throw error;
    }
  }
}; 