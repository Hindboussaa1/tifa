package com.restaurant.healthirestaurant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.healthirestaurant.callbacks.OnItemClick
import com.restaurant.healthirestaurant.databinding.ItemDashboardBinding
import com.restaurant.healthirestaurant.models.DashboardItem

class DashboardAdapter(
    private val clickListener: OnItemClick,
    private val dashBoardItemsList: List<DashboardItem>
    ) : RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val binding = ItemDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(dashBoardItemsList[position])
    }

    override fun getItemCount(): Int {
        return dashBoardItemsList.size
    }

    inner class DashboardViewHolder(private val binding: ItemDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DashboardItem) {
            binding.iconImageView.setImageResource(item.iconRes)
            binding.itemTextView.text = item.title

            binding.root.setOnClickListener { clickListener.clickListener(adapterPosition,item.title) }
        }
    }
}
