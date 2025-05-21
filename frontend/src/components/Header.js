import React, { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';

const Header = () => {
  const navigate = useNavigate();

  const handleMyPageClick = () => {
    if (user?.role === 'ROLE_ADMIN') {
      navigate('/admin');
    } else {
      navigate('/mypage');
    }
  };

  return (
    <div>
      {/* Render your component content here */}
    </div>
  );
};

export default Header; 