import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export const OAuthRedirect = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  useEffect(() => {
    const handleOAuthRedirect = () => {
      try {
        // URL에서 파라미터 추출
        const params = new URLSearchParams(location.search);
        const accessToken = params.get('accessToken');
        const refreshToken = params.get('refreshToken');
        const userId = params.get('userId');
        const nickname = params.get('nickname');
        const role = params.get('role');
        
        console.log('OAuth Redirect - 받은 role 값:', role);  // role 값 확인

        if (!accessToken || !refreshToken || !userId || !nickname || !role) {
          throw new Error('Required parameters missing');
        }

        // 사용자 정보 객체 생성
        const userInfo = {
          userId,
          nickname,
          role
        };
        console.log('localStorage에 저장할 사용자 정보:', userInfo);

        // AuthContext의 login 함수를 사용하여 로그인 처리
        login(userInfo, { accessToken, refreshToken });

        // 메인 페이지로 리디렉션
        navigate('/');
      } catch (error) {
        console.error('OAuth redirect error:', error);
        navigate('/login?error=oauth_failed');
      }
    };

    handleOAuthRedirect();
  }, [location, navigate, login]);

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <h2 className="text-2xl font-bold mb-4">로그인 처리 중...</h2>
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto"></div>
      </div>
    </div>
  );
}; 