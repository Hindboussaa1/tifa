package com.restaurant.healthirestaurant.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import com.restaurant.healthirestaurant.databinding.FragmentSetFoodPlanBinding
import com.restaurant.healthirestaurant.utils.DataProvider


class SetFoodPlanFragment : Fragment() {
    private lateinit var binding: FragmentSetFoodPlanBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetFoodPlanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialization()
        setListeners()

    }

    private fun initialization() {
        setupSpinner(binding.spinnerPlanType, DataProvider.planTypesArray)
        setupSpinner(binding.spinnerMealFrequency, DataProvider.mealFrequenciesArray)
    }

    private fun setupSpinner(spinner: Spinner, data: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setListeners() {
        binding.btnProceed.setOnClickListener {
            val selectedPlanType = binding.spinnerPlanType.selectedItem.toString()
            val selectedMealFrequency = binding.spinnerMealFrequency.selectedItem.toString()
        }

        binding.leftIcon.setOnClickListener { findNavController().popBackStack() }
    }

}