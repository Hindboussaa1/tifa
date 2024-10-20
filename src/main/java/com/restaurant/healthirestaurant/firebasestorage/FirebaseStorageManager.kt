package com.restaurant.healthirestaurant.firebasestorage

import android.app.Dialog
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageManager { private val mStorageRef = FirebaseStorage.getInstance().reference

    fun uploadImage(loader: Dialog, imageFileName: String, imageUri: Uri, callBack:(Uri) -> Unit) {

        val uploadTask = mStorageRef.child(imageFileName).putFile(imageUri)
        uploadTask.addOnSuccessListener {
            mStorageRef.child(imageFileName).downloadUrl.addOnSuccessListener { uri ->
                callBack(uri)
            }.addOnFailureListener {
                handleError(loader)
            }

        }.addOnFailureListener {
            handleError(loader, it.message)
        }
    }

        private fun handleError(loader: Dialog, errorMessage: String? = null) {
        if (loader.isShowing) loader.dismiss()
        Toast.makeText(loader.context, errorMessage ?: "An error occurred", Toast.LENGTH_SHORT).show()
    }

}