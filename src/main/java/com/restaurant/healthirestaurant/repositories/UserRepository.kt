package com.restaurant.healthirestaurant.repositories

import android.app.Dialog
import androidx.core.net.toUri
import com.restaurant.healthirestaurant.models.Restaurant
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.restaurant.healthirestaurant.firebasestorage.FirebaseStorageManager


//Business Logic
class UserRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Restaurants")
    private val storageManager = FirebaseStorageManager()

    fun  registerUser(restaurant: Restaurant, loader: Dialog, onComplete: (Boolean, String) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(restaurant.email, restaurant.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val imageFileName = "restaurant/${restaurant.restaurantName}.png"

                    storageManager.uploadImage(
                        loader,
                        imageFileName,
                        restaurant.restaurantDisplayImage.toUri()
                    ) { downloadUri ->
                        try {
                            val userId = firebaseAuth.currentUser?.uid.orEmpty()

                            val userData = restaurant.apply {
                                restaurantDisplayImage = downloadUri.toString()
                                restaurantId = userId
                            }

                            uploadUserDetails(userId, userData)
                            onComplete(true, "")
                        } catch (e: Exception) {
                            handleUploadError(e, onComplete)
                        }
                    }

                } else {
                    onComplete(false, task.exception?.message ?: "Registration failed")
                }
            }
    }


    fun loginUser(email: String, password: String, onComplete: (Boolean, String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "")
                } else {
                    onComplete(false, task.exception?.message ?: "Login failed")
                }
             }
          }


    fun signInWithGoogle(credential: AuthCredential, onComplete: (Boolean, String) -> Unit) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        val userId = user.uid
                        val userData = Restaurant(
                            restaurantId = userId,
                            restaurantName = user.displayName.orEmpty(),
                            email = user.email.orEmpty(),
                        )
                        uploadUserDetails(userId, userData)
                        onComplete(true, "")
                    } else {
                        onComplete(false, "User information not available")
                    }
                } else {
                    onComplete(false, task.exception?.message ?: "Google Sign In failed")
                }
            }
    }

    private fun uploadUserDetails(userId: String, userData: Restaurant) {
        databaseReference.child(userId).setValue(userData)
    }


    private fun handleUploadError(e: Exception, onComplete: (Boolean, String) -> Unit) {
        onComplete(false, e.message ?: "An error occurred during upload")
    }



    fun getRestaurantById(restaurantId: String, onComplete: (Restaurant?) -> Unit) {
        val restaurantRef = databaseReference.child(restaurantId)

        restaurantRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restaurant = snapshot.getValue(Restaurant::class.java)
                onComplete(restaurant)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(null)
            }
        })
    }
}
