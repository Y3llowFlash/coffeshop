package com.example.coffeeshopapp

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.model.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderHistoryAdapter(
    private val orders: MutableList<Order> = mutableListOf()
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val context = parent.context
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(24, 16, 24, 16)
            }
            setPadding(32, 24, 32, 24)
            setBackgroundColor(0xFFF3E5D8.toInt())
        }

        val titleView = TextView(context).apply {
            textSize = 20f
        }
        val itemsView = TextView(context).apply {
            textSize = 16f
            setPadding(0, 12, 0, 12)
        }
        val totalView = TextView(context).apply {
            textSize = 16f
        }
        val dateView = TextView(context).apply {
            textSize = 16f
            gravity = Gravity.END
            setPadding(0, 8, 0, 0)
        }

        container.addView(titleView)
        container.addView(itemsView)
        container.addView(totalView)
        container.addView(dateView)

        return OrderViewHolder(container, titleView, itemsView, totalView, dateView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.titleView.text = "Order"
        holder.itemsView.text = order.items.joinToString(separator = "\n") { item ->
            "${item.name} x${item.quantity}"
        }
        holder.totalView.text = "Total: $${String.format("%.2f", order.totalPrice)}"
        holder.dateView.text = "Date: ${formatDate(order)}"
    }

    override fun getItemCount(): Int = orders.size

    fun submitList(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    private fun formatDate(order: Order): String {
        val timestamp = order.createdAt ?: return "Unknown"
        return DATE_FORMAT.format(timestamp.toDate())
    }

    class OrderViewHolder(
        itemView: LinearLayout,
        val titleView: TextView,
        val itemsView: TextView,
        val totalView: TextView,
        val dateView: TextView
    ) : RecyclerView.ViewHolder(itemView)

    private companion object {
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
}
