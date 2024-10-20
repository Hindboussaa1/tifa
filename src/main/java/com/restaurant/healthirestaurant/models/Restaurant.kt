package com.restaurant.healthirestaurant.models

data class Restaurant(
    var restaurantId: String = "",
    var restaurantDisplayImage: String = "",
    val restaurantName: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val cuisineType: String = "",
)
