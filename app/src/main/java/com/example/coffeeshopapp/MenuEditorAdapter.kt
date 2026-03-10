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

class MenuEditorAdapter(
    private val items: MutableList<CoffeeModel> = mutableListOf(),
    private val onEditClick: (CoffeeModel) -> Unit,
    private val onDeleteClick: (CoffeeModel) -> Unit
) : RecyclerView.Adapter<MenuEditorAdapter.MenuItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_editor, parent, false)
        return MenuItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val coffee = items[position]
        holder.nameView.text = coffee.name
        holder.descriptionView.text = coffee.description
        holder.typesView.text = formatTypes(coffee.types)
        holder.priceView.text = formatMMK(coffee.price)

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

    private fun formatTypes(types: List<String>): String {
        return types.joinToString(separator = " • ") { type ->
            when (type) {
                "non_coffee" -> "Non Coffee"
                "eats" -> "Eats"
                else -> type.replaceFirstChar { it.uppercase() }
            }
        }
    }

    class MenuItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgMenuItem)
        val nameView: TextView = itemView.findViewById(R.id.tvMenuItemName)
        val descriptionView: TextView = itemView.findViewById(R.id.tvMenuItemDescription)
        val typesView: TextView = itemView.findViewById(R.id.tvMenuItemTypes)
        val priceView: TextView = itemView.findViewById(R.id.tvMenuItemPrice)
        val editButton: Button = itemView.findViewById(R.id.btnEditMenuItem)
        val deleteButton: Button = itemView.findViewById(R.id.btnDeleteMenuItem)
    }
}
