package com.example.coffeeshopapp

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.example.coffeeshopapp.model.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderHistoryAdapter(
    private val orders: MutableList<Order> = mutableListOf()
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val context = parent.context
        val cardView = CardView(context).apply {
            radius = 24f
            cardElevation = 10f
            useCompatPadding = true
            setCardBackgroundColor(context.getColor(R.color.cream))
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12)
            }
        }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 16, 16, 16)
        }

        val titleView = TextView(context).apply {
            textSize = 18f
            setTextColor(context.getColor(R.color.coffeeBrown))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val itemsView = TextView(context).apply {
            textSize = 16f
            setTextColor(context.getColor(R.color.mochaText))
            setPadding(0, 12, 0, 12)
        }
        val totalView = TextView(context).apply {
            textSize = 16f
            setTextColor(context.getColor(R.color.coffeeBrown))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val dateView = TextView(context).apply {
            textSize = 13f
            setTextColor(context.getColor(R.color.latte))
            setPadding(0, 6, 0, 0)
        }

        container.addView(titleView)
        container.addView(dateView)
        container.addView(itemsView)
        container.addView(totalView)
        cardView.addView(container)

        return OrderViewHolder(cardView, titleView, itemsView, totalView, dateView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.titleView.text = "Order #${order.id.takeLast(5).uppercase()}"
        holder.dateView.text = formatDate(order)
        holder.itemsView.text = order.items.joinToString(separator = "\n") { item ->
            "${item.name} x${item.quantity}"
        }
        holder.totalView.text = "Total: $${String.format("%.2f", order.totalPrice)}"
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
        itemView: CardView,
        val titleView: TextView,
        val itemsView: TextView,
        val totalView: TextView,
        val dateView: TextView
    ) : RecyclerView.ViewHolder(itemView)

    private companion object {
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
}
