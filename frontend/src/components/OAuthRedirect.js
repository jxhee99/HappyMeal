import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

export const OAuthRedirect = () => {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const handleOAuthRedirect = () => {
      try {
        // URL에서 파라미터 추출
        const params = new URLSearchParams(location.search);
        const accessToken = params.get('accessToken');
        const refreshToken = params.get('refreshToken');
        const userId = params.get('userId');
        const nickname = params.get('nickname');

        if (!accessToken || !refreshToken || !userId || !nickname) {
          throw new Error('Required parameters missing');
        }

        // 토큰 저장
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        
        // 사용자 정보 저장
        localStorage.setItem('user', JSON.stringify({
          userId,
          nickname
        }));

        // 메인 페이지로 리디렉션
        navigate('/');
      } catch (error) {
        console.error('OAuth redirect error:', error);
        navigate('/login?error=oauth_failed');
      }
    };

    handleOAuthRedirect();
  }, [location, navigate]);

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <h2 className="text-2xl font-bold mb-4">로그인 처리 중...</h2>
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
      </div>
    </div>
  );
}; 