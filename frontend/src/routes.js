import { Routes, Route } from 'react-router-dom';
import { ProtectedRoute } from './components/ProtectedRoute';
import { OAuthRedirect } from './components/OAuthRedirect';
import Login from './pages/Login';
import Home from './pages/Home';
import MealLogs from './pages/MealLogs';
import Foods from './pages/Foods';
import Board from './pages/Board';
import BoardWrite from './pages/BoardWrite';
import BoardDetail from './pages/BoardDetail';
import MyPage from './pages/MyPage';
import AdminPage from './pages/AdminPage';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/oauth/redirect" element={<OAuthRedirect />} />
      <Route path="/" element={<Home />} />
      
      {/* Protected Routes */}
      <Route element={<ProtectedRoute />}>
        <Route path="/meallogs" element={<MealLogs />} />
        <Route path="/board/write" element={<BoardWrite />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/admin" element={<AdminPage />} />
      </Route>

      {/* Public Routes */}
      <Route path="/foods" element={<Foods />} />
      <Route path="/board" element={<Board />} />
      <Route path="/board/:id" element={<BoardDetail />} />
    </Routes>
  );
};

export default AppRoutes; 