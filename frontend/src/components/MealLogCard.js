import React from "react";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";

function MealLogCard({ mealLog, food, user }) {
  return (
    <Card sx={{
      borderRadius: 4,
      boxShadow: "0 4px 16px 0 rgba(255,77,41,0.08)",
      margin: 2,
      width: 280,
      background: "#fff"
    }}>
      <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", pt: 3 }}>
        <Avatar
          src={mealLog.img_url}
          alt={food.name}
          sx={{ width: 110, height: 110, mb: 2, border: "3px solid #b2e0ff", boxShadow: 2 }}
        />
      </Box>
      <CardContent sx={{ textAlign: "center", p: 2 }}>
        <Typography variant="h6" sx={{ color: "#ff4d29", fontWeight: 700, mb: 1 }}>{food.name}</Typography>
        <Typography sx={{ color: "#555", fontSize: 15, mb: 0.5 }}>{mealLog.meal_type} · {mealLog.meal_date}</Typography>
        <Typography sx={{ color: "#888", fontSize: 14, mb: 1 }}>섭취량: {mealLog.quantity}{food.unit}</Typography>
        <Box sx={{ display: "flex", alignItems: "center", justifyContent: "center", mt: 1 }}>
          <Avatar src={user.profile_image_url} alt={user.nickname} sx={{ width: 28, height: 28, mr: 1 }} />
          <Typography sx={{ color: "#aaa", fontSize: 13 }}>{user.nickname}</Typography>
        </Box>
      </CardContent>
    </Card>
  );
}

export default MealLogCard; 