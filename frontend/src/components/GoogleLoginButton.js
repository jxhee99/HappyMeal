import React from 'react';
import { motion } from 'framer-motion';

export const GoogleLoginButton = () => {
  const handleGoogleLogin = () => {
    // 백엔드의 OAuth2 시작점으로 리디렉션
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  return (
    <motion.button
      whileHover={{ scale: 1.05 }}
      whileTap={{ scale: 0.95 }}
      onClick={handleGoogleLogin}
      className="flex items-center justify-center w-full px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
    >
      <img
        src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
        alt="Google logo"
        className="w-5 h-5 mr-2"
      />
      Google로 계속하기
    </motion.button>
  );
}; 