package com.restaurant.healthirestaurant.utils

import com.restaurant.healthirestaurant.R
import com.restaurant.healthirestaurant.models.DashboardItem

object DataProvider {

    val planTypesArray: Array<String> = arrayOf("Week", "15 Days", "Month")

    // Meal frequencies array
    val mealFrequenciesArray: Array<String> = arrayOf("Once a day", "Twice a day", "Thrice a day")

    fun getDashBoardItemsData(): List<DashboardItem> {
        return mutableListOf(
            DashboardItem(R.drawable.addfood_icon, "Add Food"),
            DashboardItem(R.drawable.food_plan_icon, "Set Food Plan"),
            DashboardItem(R.drawable.addfood_icon, "View Food"),
            DashboardItem(R.drawable.food_plan_icon, "View Food Plan"),
            )
    }
}