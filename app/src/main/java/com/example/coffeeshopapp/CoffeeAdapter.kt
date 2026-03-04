package com.example.coffeeshopapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CoffeeAdapter(
    private val coffeeList: List<CoffeeModel>,
    private val onItemClick: (CoffeeModel) -> Unit // A function to handle clicks later
) : RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>() {

    // 1. Setup the Look: Create the view from the XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coffee, parent, false)
        return CoffeeViewHolder(view)
    }

    // 2. Setup the Data: Bind data to the view
    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffee = coffeeList[position]
        holder.tvName.text = coffee.name
        holder.tvPrice.text = "$${coffee.price}"
        holder.imgCoffee.setImageResource(coffee.imageResId)

        // Handle the click (We will use this in Round 3)
        holder.itemView.setOnClickListener {
            onItemClick(coffee)
        }
    }

    // 3. How many items?
    override fun getItemCount(): Int = coffeeList.size

    // Inner Class: Holds the UI elements to save memory
    class CoffeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCoffee: ImageView = itemView.findViewById(R.id.imgCoffee)
        val tvName: TextView = itemView.findViewById(R.id.tvCoffeeName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCoffeePrice)
    }
}