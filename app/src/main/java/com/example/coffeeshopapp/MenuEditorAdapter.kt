package com.example.coffeeshopapp

import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshopapp.model.CoffeeModel

class MenuEditorAdapter(
    private val items: MutableList<CoffeeModel> = mutableListOf(),
    private val onEditClick: (CoffeeModel) -> Unit,
    private val onDeleteClick: (CoffeeModel) -> Unit
) : RecyclerView.Adapter<MenuEditorAdapter.MenuItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val context = parent.context
        val cardView = CardView(context).apply {
            radius = 24f
            cardElevation = 8f
            useCompatPadding = true
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
        }

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(144, 144)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(24, 0, 24, 0)
        }

        val nameView = TextView(context).apply {
            textSize = 18f
            setTextColor(context.getColor(R.color.coffeeBrown))
        }
        val priceView = TextView(context).apply {
            textSize = 16f
            setTextColor(context.getColor(R.color.mochaText))
        }

        content.addView(nameView)
        content.addView(priceView)

        val actions = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val editButton = Button(context).apply {
            text = "Edit"
        }
        val deleteButton = Button(context).apply {
            text = "Delete"
        }

        actions.addView(editButton)
        actions.addView(deleteButton)

        root.addView(imageView)
        root.addView(content)
        root.addView(actions)
        cardView.addView(root)

        return MenuItemViewHolder(cardView, imageView, nameView, priceView, editButton, deleteButton)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val coffee = items[position]
        holder.nameView.text = coffee.name
        holder.priceView.text = "$${String.format("%.2f", coffee.price)}"

        if (coffee.imageUrl.isNotBlank()) {
            Glide.with(holder.imageView.context)
                .load(coffee.imageUrl)
                .into(holder.imageView)
        } else if (coffee.imageDrawable.isNotBlank()) {
            val context = holder.imageView.context
            val resId = context.resources.getIdentifier(
                coffee.imageDrawable,
                "drawable",
                context.packageName
            )
            holder.imageView.setImageResource(resId)
        } else {
            holder.imageView.setImageResource(coffee.imageResId)
        }

        holder.editButton.setOnClickListener { onEditClick(coffee) }
        holder.deleteButton.setOnClickListener { onDeleteClick(coffee) }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<CoffeeModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class MenuItemViewHolder(
        itemView: CardView,
        val imageView: ImageView,
        val nameView: TextView,
        val priceView: TextView,
        val editButton: Button,
        val deleteButton: Button
    ) : RecyclerView.ViewHolder(itemView)
}
