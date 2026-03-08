package com.example.coffeeshopapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.model.CoffeeModel

class CoffeeAdapter(
    private var coffeeList: List<CoffeeModel>,
    private val onItemClick: (CoffeeModel) -> Unit,
    private val onAddToCartClick: (CoffeeModel) -> Unit
) : RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coffee, parent, false)
        return CoffeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffee = coffeeList[position]
        holder.tvName.text = coffee.name
        holder.tvPrice.text = "$${coffee.price}"
        holder.imgCoffee.setImageResource(coffee.imageResId)

        holder.itemView.setOnClickListener {
            onItemClick(coffee)
        }
        holder.btnAddToCart.setOnClickListener {
            onAddToCartClick(coffee)
        }
    }

    override fun getItemCount(): Int = coffeeList.size

    fun updateData(updatedList: List<CoffeeModel>) {
        coffeeList = updatedList
        notifyDataSetChanged()
    }

    class CoffeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCoffee: ImageView = itemView.findViewById(R.id.imgCoffee)
        val tvName: TextView = itemView.findViewById(R.id.tvCoffeeName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCoffeePrice)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
    }
}
