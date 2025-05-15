import React from "react";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import LinearProgress from "@mui/material/LinearProgress";

// 목업 데이터
const stats = {
  date: "2025-05-10",
  consumed_calories: 1850,
  consumed_protein: 120,
  consumed_carbs: 200,
  consumed_fat: 60,
  target_calories: 2000,
  target_protein: 130,
  target_carbs: 250,
  target_fat: 70
};

function StatBar({ label, value, max, color }) {
  return (
    <Box sx={{ mb: 2 }}>
      <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.5 }}>
        <Typography sx={{ fontWeight: 500 }}>{label}</Typography>
        <Typography sx={{ fontWeight: 500 }}>{value} / {max}</Typography>
      </Box>
      <LinearProgress variant="determinate" value={Math.min((value / max) * 100, 100)} sx={{ height: 10, borderRadius: 5, background: "#f2f2f2", '& .MuiLinearProgress-bar': { background: color } }} />
    </Box>
  );
}

function TodayStats() {
  return (
    <Box sx={{ mb: 5, p: 3, background: "#fff", borderRadius: 3, boxShadow: "0 2px 8px rgba(255,77,41,0.07)" }}>
      <Typography variant="h6" sx={{ color: "#ff4d29", fontWeight: 700, mb: 2 }}>오늘의 영양 통계</Typography>
      <StatBar label="칼로리 (kcal)" value={stats.consumed_calories} max={stats.target_calories} color="#ff4d29" />
      <StatBar label="탄수화물 (g)" value={stats.consumed_carbs} max={stats.target_carbs} color="#6ec6ff" />
      <StatBar label="단백질 (g)" value={stats.consumed_protein} max={stats.target_protein} color="#4caf50" />
      <StatBar label="지방 (g)" value={stats.consumed_fat} max={stats.target_fat} color="#ffd600" />
    </Box>
  );
}

export default TodayStats; 