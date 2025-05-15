import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const refreshToken = async (refreshToken) => {
  try {
    const response = await axios.post(`${API_URL}/api/auth/refresh`, { refreshToken });
    return response.data;
  } catch (error) {
    console.error('Token refresh error:', error);
    throw error;
  }
}; 