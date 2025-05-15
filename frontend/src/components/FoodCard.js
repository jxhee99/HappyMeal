import React from "react";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

function FoodCard({ food }) {
  return (
    <Card sx={{
      borderRadius: 4,
      boxShadow: "0 4px 16px 0 rgba(255,77,41,0.08)",
      margin: 2,
      width: 240,
      background: "#fff"
    }}>
      <Box sx={{
        width: 96,
        height: 96,
        borderRadius: "50%",
        background: "#b2e0ff",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontSize: 40,
        color: "#ff4d29",
        mx: "auto",
        mt: 3,
        mb: 2
      }}>{food.name[0]}</Box>
      <CardContent sx={{ textAlign: "center", p: 2 }}>
        <Typography variant="h6" sx={{ color: "#ff4d29", fontWeight: 700, mb: 1 }}>{food.name}</Typography>
        <Typography sx={{ color: "#555", fontSize: 15, mb: 0.5 }}>칼로리: {food.calories}kcal</Typography>
        <Typography sx={{ color: "#888", fontSize: 14 }}>탄수화물: {food.carbs}g · 단백질: {food.protein}g · 지방: {food.fat}g</Typography>
        <Typography sx={{ color: "#aaa", fontSize: 13, mt: 1 }}>1회 제공량: {food.serving_size}{food.unit}</Typography>
      </CardContent>
    </Card>
  );
}

export default FoodCard; 