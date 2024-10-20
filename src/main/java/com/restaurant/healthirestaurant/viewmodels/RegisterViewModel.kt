package com.restaurant.healthirestaurant.viewmodels

import android.app.Dialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.healthirestaurant.models.Restaurant
import com.restaurant.healthirestaurant.repositories.UserRepository
import com.google.firebase.auth.AuthCredential



class RegisterViewModel(private val  repository: UserRepository) : ViewModel()  {
    private val _loginResult = MutableLiveData<Pair<Boolean, String>>()
    val loginResult: LiveData<Pair<Boolean, String>> get() = _loginResult

    //getting info from loginActivity and again passing it to repository
    fun loginUser(email: String, password: String) {
        repository.loginUser(email, password) { success, message ->
            _loginResult.value = Pair(success, message)
        }
    }

    fun firebaseAuthWithGoogle(credential: AuthCredential) {
        repository.signInWithGoogle(credential) { success, message ->
            _loginResult.value = Pair(success, message)
        }
    }

    fun registerUser(restaurant: Restaurant, loader: Dialog, onComplete: (Boolean, String) -> Unit) {
        repository.registerUser(restaurant, loader, onComplete)
    }

    fun getRestaurantById(restaurantId: String, onComplete: (Restaurant?) -> Unit) {
        repository.getRestaurantById(restaurantId, onComplete)
    }
}
