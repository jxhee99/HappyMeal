import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AdminRoute } from './components/AdminRoute';
import { OAuthRedirect } from './components/OAuthRedirect';
import Login from './pages/Login';
import Home from './pages/Home';
import MealLogs from './pages/MealLogs';
import Foods from './pages/Foods';
import Board from './pages/Board';
import BoardWrite from './pages/BoardWrite';
import BoardDetail from './pages/BoardDetail';
import MyPage from './pages/MyPage';
import Admin from './pages/Admin';
import Register from './pages/Register';
import Profile from './pages/Profile';
import PrivateRoute from './components/PrivateRoute';

const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/oauth/redirect" element={<OAuthRedirect />} />
      <Route path="/foods" element={<Foods />} />
      <Route path="/board" element={<Board />} />
      <Route path="/board/:id" element={<BoardDetail />} />
      
      {/* Protected Routes */}
      <Route element={<ProtectedRoute />}>
        <Route path="/meallogs" element={<MealLogs />} />
        <Route path="/board/write" element={<BoardWrite />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/profile" element={<Profile />} />
      </Route>

      {/* Admin Routes */}
      <Route
        path="/admin"
        element={
          <AdminRoute>
            <Admin />
          </AdminRoute>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default AppRoutes; 