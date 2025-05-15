import React from "react";
import { foods } from "../mock/foods";
import FoodCard from "../components/FoodCard";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";

function Foods() {
  return (
    <Box sx={{ background: "#eaf6ff", minHeight: "100vh", py: 6 }}>
      <Typography variant="h4" sx={{ color: "#ff4d29", fontWeight: 700, textAlign: "center", mb: 5 }}>
        음식 리스트
      </Typography>
      <Grid container spacing={2} justifyContent="center">
        {foods.map(food => (
          <Grid item key={food.food_id}>
            <FoodCard food={food} />
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

export default Foods; 