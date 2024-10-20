package com.restaurant.healthirestaurant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restaurant.healthirestaurant.R
import com.restaurant.healthirestaurant.databinding.ItemFoodLayoutBinding
import com.restaurant.healthirestaurant.models.FoodItems

class FoodListAdapter :
    RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    private val foodItemsList: MutableList<FoodItems> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding =
            ItemFoodLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodItemsList[position]
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int {
        return foodItemsList.size
    }

    fun submitList(newFoodList: List<FoodItems>) {
        foodItemsList.clear()
        foodItemsList.addAll(newFoodList)
        notifyDataSetChanged()
    }

    class FoodViewHolder(private val binding: ItemFoodLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foodItem: FoodItems) {
            binding.foodNameTextView.text = foodItem.foodTitle
            binding.caloriesTextView.text = "Calories: ${foodItem.foodCalories}"
            binding.priceTextView.text = "Price: $${foodItem.foodPrice}"

            Glide.with(binding.root.context)
                .load(foodItem.foodImageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.foodImageView)
        }
    }
}
