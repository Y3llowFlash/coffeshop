package com.example.coffeeshopapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshopapp.model.CoffeeModel

class CoffeeAdapter(
    coffeeList: List<CoffeeModel>,
    private val onItemClick: (CoffeeModel) -> Unit,
    private val onAddToCartClick: (CoffeeModel) -> Unit
) : RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>() {
    private var originalCoffeeList = coffeeList.toList()
    private var displayedCoffeeList = coffeeList.toList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coffee, parent, false)
        return CoffeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffee = displayedCoffeeList[position]
        holder.tvName.text = coffee.name
        holder.tvDescription.text = coffee.description
        holder.tvPrice.text = "${coffee.price} MMK"
        if (coffee.imageUrl.isNotBlank()) {
            Glide.with(holder.imgCoffee.context)
                .load(coffee.imageUrl)
                .into(holder.imgCoffee)
        } else if (coffee.imageDrawable.isNotBlank()) {
            val resId = holder.imgCoffee.context.resources.getIdentifier(
                coffee.imageDrawable,
                "drawable",
                holder.imgCoffee.context.packageName
            )
            if (resId != 0) {
                holder.imgCoffee.setImageResource(resId)
            } else {
                holder.imgCoffee.setImageResource(coffee.imageResId)
            }
        } else {
            holder.imgCoffee.setImageResource(coffee.imageResId)
        }

        holder.itemView.setOnClickListener {
            onItemClick(coffee)
        }
        holder.btnAddToCart.setOnClickListener {
            holder.itemView.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    holder.itemView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
            onAddToCartClick(coffee)
        }
    }

    override fun getItemCount(): Int = displayedCoffeeList.size

    fun filter(query: String, selectedType: String) {
        val trimmedQuery = query.trim()
        displayedCoffeeList = originalCoffeeList.filter { coffee ->
            val matchesQuery = trimmedQuery.isBlank() ||
                coffee.name.contains(trimmedQuery, ignoreCase = true)
            val matchesType = selectedType == "all" || coffee.types.contains(selectedType)
            matchesQuery && matchesType
        }
        notifyDataSetChanged()
    }

    fun updateData(newCoffeeList: List<CoffeeModel>) {
        originalCoffeeList = newCoffeeList.toList()
        displayedCoffeeList = newCoffeeList.toList()
        notifyDataSetChanged()
    }

    class CoffeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCoffee: ImageView = itemView.findViewById(R.id.imgCoffee)
        val tvName: TextView = itemView.findViewById(R.id.tvCoffeeName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvCoffeeDescription)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCoffeePrice)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
    }
}
