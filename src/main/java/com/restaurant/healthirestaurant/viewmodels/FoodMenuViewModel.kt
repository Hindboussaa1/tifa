package com.restaurant.healthirestaurant.viewmodels

import android.app.Dialog
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.healthirestaurant.models.FoodItems
import com.restaurant.healthirestaurant.repositories.FoodRepository


class FoodMenuViewModel(private val repository: FoodRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Pair<Boolean, String>>()
    val uploadResult: LiveData<Pair<Boolean, String>> get() = _uploadResult

    private val _foodItems = MutableLiveData<Triple<List<FoodItems>, Boolean, String>>()
    val foodItems: MutableLiveData<Triple<List<FoodItems>, Boolean, String>> get() = _foodItems

    fun uploadFoodItemData(
        name: String,
        calories: String,
        price: String,
        uri: Uri?,
        loader: Dialog
    ) {
        repository.uploadFoodItemData(name, calories, price, uri, loader) { success, message ->
            _uploadResult.value = Pair(success, message)
        }
    }

    fun fetchFoodItems() {
        repository.fetchFoodItems { foodItemsList, flag, msg ->
            _foodItems.value = Triple(foodItemsList, flag, msg)
        }
    }
}
