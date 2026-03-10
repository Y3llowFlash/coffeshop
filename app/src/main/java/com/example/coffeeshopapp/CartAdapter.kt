package com.example.coffeeshopapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.model.CartItem

class CartAdapter(
    private val cartList: List<CartItem>,
    private val onIncreaseQuantity: (CartItem, Int) -> Unit,
    private val onDecreaseQuantity: (CartItem, Int) -> Unit
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]

        holder.tvName.text = item.coffee.name
        holder.tvQty.text = "${item.quantity}x"

        val rowPrice = item.coffee.price * item.quantity
        holder.tvPrice.text = formatMMK(rowPrice)

        holder.btnPlus.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onIncreaseQuantity(item, position)
            }
        }

        holder.btnMinus.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onDecreaseQuantity(item, position)
            }
        }
    }

    override fun getItemCount(): Int = cartList.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnMinus: ImageButton = itemView.findViewById(R.id.btnCartMinus)
        val tvName: TextView = itemView.findViewById(R.id.tvCartName)
        val tvQty: TextView = itemView.findViewById(R.id.tvCartQty)
        val btnPlus: ImageButton = itemView.findViewById(R.id.btnCartPlus)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
    }
}
