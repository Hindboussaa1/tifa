package com.restaurant.healthirestaurant.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.restaurant.healthirestaurant.repositories.FoodRepository
import com.restaurant.healthirestaurant.viewmodels.FoodMenuViewModel

class FoodViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodMenuViewModel::class.java)) {
            return FoodMenuViewModel(FoodRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
