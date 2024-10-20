package com.restaurant.healthirestaurant.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.restaurant.healthirestaurant.R
import com.restaurant.healthirestaurant.adapters.DashboardAdapter
import com.restaurant.healthirestaurant.callbacks.OnItemClick
import com.restaurant.healthirestaurant.databinding.FragmentHomeBinding
import com.restaurant.healthirestaurant.models.Restaurant
import com.restaurant.healthirestaurant.repositories.UserRepository
import com.restaurant.healthirestaurant.ui.activities.LoginActivity
import com.restaurant.healthirestaurant.utils.DataProvider
import com.restaurant.healthirestaurant.utils.LoadingDialog
import com.restaurant.healthirestaurant.utils.MAIN_MENU
import com.restaurant.healthirestaurant.viewmodelfactory.RegisterViewModelFactory
import com.restaurant.healthirestaurant.viewmodels.RegisterViewModel
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class HomeFragment : Fragment(), OnItemClick {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var dashboardAdapter: DashboardAdapter
    private lateinit var viewModel: RegisterViewModel
    private val timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialization()
        setRecyclerView()
        setListeners()
    }


    private fun initialization() {
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(UserRepository()))[RegisterViewModel::class.java]
        fetchUserInfo()
    }


    private fun fetchUserInfo() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.uid?.let { userId ->
            viewModel.getRestaurantById(userId) { restaurant ->
                restaurant?.let { restaurantData ->
                    updateUIWithRestaurantData(restaurantData, currentUser)
                }
            }
        }
    }

    private fun updateUIWithRestaurantData(restaurantData: Restaurant, currentUser: FirebaseUser) {
        binding.apply {
            personNameTextView.text = restaurantData.restaurantName
            personImageView.loadImage(restaurantData.getDisplayImageUrl() ?: currentUser.photoUrl)
        }
    }

    private fun Restaurant.getDisplayImageUrl(): Uri? {
        return if (restaurantDisplayImage.isNotBlank()) {
            restaurantDisplayImage.toUri()
        } else {
            null
        }
    }

    private fun ImageView.loadImage(imageUrl: Uri?) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.profile_icon)
            .error(R.drawable.profile_icon)
            .into(this)
    }


    private fun setListeners() {
        binding.logOutTv.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        navigateToLoginActivity()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun setTimerToScroll(layoutManager: LinearLayoutManager) {
        // Schedule automatic scrolling every 10 seconds
        timer.scheduleAtFixedRate(10 * 1000, 10 * 1000) {
            requireActivity().runOnUiThread {
                val currentPosition = layoutManager.findFirstVisibleItemPosition()
                val nextPosition =
                    if (currentPosition < layoutManager.itemCount - 1) currentPosition + 1 else 0
                binding.viewPagerDashboard.smoothScrollToPosition(nextPosition)
            }
        }
    }


    private fun setRecyclerView() {
        binding.apply {
            dashboardRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 2)

            setTimerToScroll(
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                .also { viewPagerDashboard.layoutManager = it }
            )

            dashboardAdapter = DashboardAdapter(this@HomeFragment, DataProvider.getDashBoardItemsData())
//            newsAdapter = NewsAdapter()
//            viewPagerDashboard.adapter = newsAdapter
            dashboardRecyclerView.adapter = dashboardAdapter
        }
    }

    override fun clickListener(position: Int, value: String) {
        openFragment(value)
    }

    private fun openFragment(title: String) {
        val bundle = Bundle().apply {
            putString(MAIN_MENU, title)
        }

        findNavController().apply {
            when (title) {
                "Add Food" -> navigate(R.id.action_homeFragment_to_addFoodMenu, bundle)
                "Set Food Plan" -> navigate(R.id.action_homeFragment_to_setFoodPlanFragment, bundle)
                "View Food" -> navigate(R.id.action_homeFragment_to_viewFoodListFragment, bundle)
                "View Food Plan" -> navigate(R.id.action_homeFragment_to_addFoodMenu, bundle)
                else -> navigate(R.id.action_homeFragment_to_addFoodMenu)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}