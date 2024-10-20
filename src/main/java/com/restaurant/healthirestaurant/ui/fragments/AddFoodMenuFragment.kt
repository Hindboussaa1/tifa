package com.restaurant.healthirestaurant.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.restaurant.healthirestaurant.R
import com.restaurant.healthirestaurant.databinding.FragmentAddFoodMenuBinding
import com.restaurant.healthirestaurant.enums.BottomSheetSelection
import com.restaurant.healthirestaurant.utils.SelectBottomSheet
import com.restaurant.healthirestaurant.utils.Utils
import com.restaurant.healthirestaurant.utils.Utils.showToast
import com.restaurant.healthirestaurant.viewmodelfactory.FoodViewModelFactory
import com.restaurant.healthirestaurant.viewmodels.FoodMenuViewModel
import java.io.ByteArrayOutputStream


class AddFoodMenuFragment : Fragment() {
    private lateinit var binding: FragmentAddFoodMenuBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var loader: Dialog
    private var bitmap: Bitmap?=null
    private var uri: Uri?=null
    private var imgURI: Uri? = null
    private var foodName:String?=null
    private var price:String?=null
    private var calories:String?=null
    private val Camera_REQUEST_CODE = 200
    private val Gallery_REQUEST_CODE = 100
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private lateinit var viewModel: FoodMenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFoodMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        buttonClicks()
        observeUploadResult()
    }

    private fun initViews() {
        viewModel = ViewModelProvider(this, FoodViewModelFactory())[FoodMenuViewModel::class.java]
        loader = Utils.progressDialog(requireActivity())
        auth = Firebase.auth
        ref = FirebaseDatabase.getInstance().reference
    }

    private fun observeUploadResult() {
        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            if (result.first) {
                showToast(requireActivity(), result.second)
                clearFields()
            } else {
                showToast(requireActivity(), "Failed to upload data ${result.second}")
            }

            if (loader.isShowing) loader.dismiss()
        }
    }

    private fun buttonClicks() {
        binding.addFoodBtn.setOnClickListener { uploadFoodItemData() }
        binding.leftIcon.setOnClickListener { findNavController().popBackStack() }
        binding.uploadImageButton.setOnClickListener { showImageSelectionBottomSheet() }
    }

    private fun showImageSelectionBottomSheet() {
        SelectBottomSheet().apply {
            callback = { selection ->
                when (selection) {
                    BottomSheetSelection.SELECT_CAMERA -> cameraImage()
                    BottomSheetSelection.SELECT_GALLERY -> selectGalleryImage()
                }
            }
        }.show(requireActivity().supportFragmentManager, "show")
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission if it is not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
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
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Start the camera activity and wait for the result
            startActivityForResult(takePictureIntent, Camera_REQUEST_CODE)
        } else {
            Toast.makeText(requireActivity(), "Failed to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Camera_REQUEST_CODE && data != null){
            val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
            uri = getImageUri(requireActivity(),bitmap)
            binding.foodImageView.setImageURI(uri)
        }

        if (requestCode == Camera_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The image is captured successfully, you can do further processing here
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uri = getImageUri(requireActivity(),imageBitmap)
            binding.foodImageView.setImageURI(uri)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == Gallery_REQUEST_CODE){
            uri = data?.data
            binding.foodImageView.setImageURI(uri) // handle chosen image
            binding.foodImageView.tag = bitmap
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


    private fun uploadFoodItemData() {
        foodName = binding.foodNameEditText.text.toString()
        calories = binding.priceEditText.text.toString()
        price = binding.caloriesEditText.text.toString()
        imgURI = uri

        if (imgURI == null) {
            showToast(requireActivity(),"Please select an image first")
            return
        }

        if (foodName.isNullOrBlank() || price.isNullOrBlank() || calories.isNullOrBlank()) {
            showToast(requireActivity(),"Please complete the details")
            return
        }

        if (!loader.isShowing)  loader.show()
       viewModel.uploadFoodItemData(foodName!!, calories!!, price!!, imgURI, loader)
    }


    private fun clearFields() {
        binding.foodNameEditText.text.clear()
        binding.priceEditText.text.clear()
        binding.caloriesEditText.text.clear()
        binding.foodImageView.setImageResource(R.drawable.ic_camera)
    }

}