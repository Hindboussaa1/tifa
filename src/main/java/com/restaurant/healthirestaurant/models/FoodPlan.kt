package com.restaurant.healthirestaurant.models

data class FoodPlan(
    val planType: String = "",
    val foodItems: List<FoodItems> = emptyList()
)