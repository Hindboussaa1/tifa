package com.restaurant.healthirestaurant.ui.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.restaurant.healthirestaurant.databinding.ActivityRegisterBinding
import com.restaurant.healthirestaurant.enums.BottomSheetSelection
import com.restaurant.healthirestaurant.models.Restaurant
import com.restaurant.healthirestaurant.repositories.UserRepository
import com.restaurant.healthirestaurant.utils.SelectBottomSheet
import com.restaurant.healthirestaurant.utils.Utils
import com.restaurant.healthirestaurant.utils.Utils.showToast
import com.restaurant.healthirestaurant.viewmodelfactory.RegisterViewModelFactory
import com.restaurant.healthirestaurant.viewmodels.RegisterViewModel
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private var bitmap: Bitmap?=null
    private lateinit var loader: Dialog
    private var uri: Uri?=null
    private val Camera_REQUEST_CODE = 200
    private val Gallery_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initValues()
        buttonClicks()
    }

        private fun initValues() {
        loader = Utils.progressDialog(this)
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(UserRepository()))[RegisterViewModel::class.java]
    }


    private fun buttonClicks() {
        binding.registerBtn.setOnClickListener {
            val user = getUserFromInput()

            if (!loader.isShowing)  loader.show()
            viewModel.registerUser(user, loader) { success, message ->
                handleRegistrationResult(success, message)
            }
        }

        binding.selectImageButton.setOnClickListener { showImageSelectionBottomSheet() }

        binding.alreadyAccountBtn.setOnClickListener {
            finish()
        }
    }

    private fun showImageSelectionBottomSheet() {
        SelectBottomSheet().apply {
            callback = { selection ->
                when (selection) {
                    BottomSheetSelection.SELECT_CAMERA -> cameraImage()
                    BottomSheetSelection.SELECT_GALLERY -> selectGalleryImage()
                }
            }
        }.show(supportFragmentManager, "show")
    }





    /**
     * Handling Images from and Gallery
     */
    private fun selectGalleryImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Gallery_REQUEST_CODE)
    }


    private fun cameraImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission if it is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, start the camera activity
            startCamera()
        }

    }

    private fun startCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Check if there is a camera app available to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Start the camera activity and wait for the result
            startActivityForResult(takePictureIntent, Camera_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Camera_REQUEST_CODE && data != null){
            val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
            uri = getImageUri(this,bitmap)
            binding.profileImage.setImageURI(uri)
        }

        if (requestCode == Camera_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The image is captured successfully, you can do further processing here
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uri = getImageUri(this, imageBitmap)
            binding.profileImage.setImageURI(uri)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == Gallery_REQUEST_CODE){
            uri = data?.data
            binding.profileImage.setImageURI(uri) // handle chosen image
            binding.profileImage.tag = bitmap
        }
    }


    /** To Convert Bitmap into Uri
     * @param inContext as Context
     * @param inImage as Bitmap
     * */
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }



    private fun handleRegistrationResult(success: Boolean, message: String) {
        if (success) {
            showToast(this,"Account Created successful")
            finish()
        } else {
            showToast(this,"Registration failed: $message")
        }
    }

    private fun getUserFromInput(): Restaurant {
        return Restaurant(
            restaurantName = binding.restaurantName.text.toString().trim(),
            restaurantDisplayImage = uri.toString(),
            email = binding.email.text.toString().trim(),
            password = binding.password.text.toString().trim(),
            phoneNumber = binding.phoneNumber.text.toString().trim(),
            address = binding.address.text.toString().trim(),
            cuisineType = binding.cuisineType.text.toString().trim(),
        )

    }


}