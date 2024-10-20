package com.restaurant.healthirestaurant.models

data class FoodItems(
    var foodTitle: String? = null,
    var foodCalories: String? = null,
    var foodPrice: String? = null,
    var foodImageUrl: String? = null,
    var restaurantID:String?=null,
    var quantity: String? = null
)
