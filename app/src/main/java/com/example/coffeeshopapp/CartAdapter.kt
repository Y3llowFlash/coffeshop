package com.example.coffeeshopapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.model.CartItem

class CartAdapter(private val cartList: List<CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]

        holder.tvName.text = item.coffee.name
        holder.tvQty.text = "${item.quantity}x"

        // Calculate price for this specific row (Price * Qty)
        val rowPrice = item.coffee.price * item.quantity
        holder.tvPrice.text = "$${String.format("%.2f", rowPrice)}"
    }

    override fun getItemCount(): Int = cartList.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCartName)
        val tvQty: TextView = itemView.findViewById(R.id.tvCartQty)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
    }
}
