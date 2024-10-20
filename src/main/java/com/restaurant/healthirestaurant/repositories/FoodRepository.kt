package com.restaurant.healthirestaurant.repositories

import android.app.Dialog
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.restaurant.healthirestaurant.firebasestorage.FirebaseStorageManager
import com.restaurant.healthirestaurant.models.FoodItems


class FoodRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val storageManager = FirebaseStorageManager()

    fun uploadFoodItemData(
        name: String,
        calories: String,
        price: String,
        uri: Uri?,
        loader: Dialog,
        onComplete: (Boolean, String) -> Unit  //Callback
    ) {
        val imageFileName = "food/${name}.png"

        storageManager.uploadImage(loader, imageFileName, uri!!) { downloadUri ->
            try {
                val foodData = FoodItems(name, calories, price, downloadUri.toString())
                uploadFoodDetails(name, foodData)
                onComplete(true, "Data Uploaded Successfully")
            } catch (e: Exception) {
                handleUploadError(e, onComplete)
            }
        }
    }


    fun fetchFoodItems(onComplete: (List<FoodItems>, Boolean, String) -> Unit) {
        databaseReference.child("FoodList").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foodItemsList = mutableListOf<FoodItems>()
                for (childSnapshot in snapshot.children) {
                    val foodItem = childSnapshot.getValue(FoodItems::class.java)
                    foodItem?.let { foodItemsList.add(it) }
                }
                onComplete(foodItemsList, true, "")
            }

            override fun onCancelled(error: DatabaseError) {
                handleUploadError(error, onComplete)
            }
        })
    }


    private fun handleUploadError(e: Exception, onComplete: (Boolean, String) -> Unit) {
        onComplete(false, e.message ?: "An error occurred during upload")
    }

    private fun handleUploadError(e: DatabaseError, onComplete: (List<FoodItems>, Boolean, String) -> Unit) {
        onComplete(emptyList(), false, e.message)
    }

    private fun uploadFoodDetails(name: String, foodData: FoodItems) {
        databaseReference.child("FoodList").child(name).setValue(foodData)
    }
}
