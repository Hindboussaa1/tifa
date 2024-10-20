package com.restaurant.healthirestaurant.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.restaurant.healthirestaurant.adapters.FoodListAdapter
import com.restaurant.healthirestaurant.databinding.FragmentViewFoodListBinding
import com.restaurant.healthirestaurant.utils.Utils
import com.restaurant.healthirestaurant.utils.Utils.setHandler
import com.restaurant.healthirestaurant.utils.Utils.showToast
import com.restaurant.healthirestaurant.viewmodelfactory.FoodViewModelFactory
import com.restaurant.healthirestaurant.viewmodels.FoodMenuViewModel


class ViewFoodListFragment : Fragment() {
    private lateinit var binding: FragmentViewFoodListBinding
    private lateinit var viewModel: FoodMenuViewModel
    private lateinit var foodListAdapter: FoodListAdapter
    private lateinit var loader: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewFoodListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialization()
        fetchData()
        setupRecyclerView()
        setListeners()
        setObservers()
        setHandler(loader)
    }

    private fun setListeners() {
        binding.leftIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initialization() {
        loader = Utils.progressDialog(requireActivity())
        viewModel = ViewModelProvider(this, FoodViewModelFactory())[FoodMenuViewModel::class.java]
    }

    private fun fetchData() {
        if (!loader.isShowing)  loader.show()
        viewModel.fetchFoodItems()
    }

    private fun setupRecyclerView() {
        foodListAdapter = FoodListAdapter()
        binding.recyclerView.adapter = foodListAdapter
    }

    private fun setObservers() {
        viewModel.foodItems.observe(viewLifecycleOwner) { result->
            if (result.second)
                foodListAdapter.submitList(result.first)
            else
                showToast(requireActivity(), "Failed to fetch data ${result.third}")

            if (loader.isShowing) loader.dismiss()
        }
    }
}