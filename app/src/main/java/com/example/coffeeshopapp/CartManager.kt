package com.example.coffeeshopapp

import android.content.Context
import com.example.coffeeshopapp.model.CartItem
import com.example.coffeeshopapp.model.CoffeeModel
import org.json.JSONArray
import org.json.JSONObject

object CartManager {
    private const val PREFS_NAME = "coffee_shop_cart"
    private const val KEY_CART_SIZE = "cart_size"
    private const val KEY_TOTAL_PRICE = "total_price"
    private const val KEY_CART_ITEMS = "cart_items"

    private val items = mutableListOf<CartItem>()

    fun addItem(coffee: CoffeeModel, quantity: Int = 1) {
        val existingItem = items.find { it.coffee.id == coffee.id }
        val safeQuantity = quantity.coerceAtLeast(1)

        if (existingItem != null) {
            existingItem.quantity += safeQuantity
        } else {
            items.add(CartItem(coffee, safeQuantity))
        }
    }

    fun getCartItems(): List<CartItem> {
        return items
    }

    fun getTotalPrice(): Double {
        var total = 0.0
        for (item in items) {
            total += (item.coffee.price * item.quantity)
        }
        return total
    }

    fun clearCart() {
        items.clear()
    }

    fun increaseItemQuantity(coffeeId: Int) {
        val existingItem = items.find { it.coffee.id == coffeeId } ?: return
        existingItem.quantity++
    }

    fun decreaseItemQuantity(coffeeId: Int) {
        val existingItem = items.find { it.coffee.id == coffeeId } ?: return
        existingItem.quantity--
        if (existingItem.quantity <= 0) {
            items.remove(existingItem)
        }
    }

    fun saveCart(context: Context) {
        val cartItemsJson = JSONArray()
        for (item in items) {
            val itemJson = JSONObject()
                .put("coffeeId", item.coffee.id)
                .put("name", item.coffee.name)
                .put("description", item.coffee.description)
                .put("price", item.coffee.price)
                .put("types", JSONArray(item.coffee.types))
                .put("imageResId", item.coffee.imageResId)
                .put("imageDrawable", item.coffee.imageDrawable)
                .put("imageUrl", item.coffee.imageUrl)
                .put("quantity", item.quantity)
            cartItemsJson.put(itemJson)
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_CART_SIZE, items.sumOf { it.quantity })
            .putFloat(KEY_TOTAL_PRICE, getTotalPrice().toFloat())
            .putString(KEY_CART_ITEMS, cartItemsJson.toString())
            .apply()
    }

    fun loadCart(context: Context) {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedItems = preferences.getString(KEY_CART_ITEMS, null)

        items.clear()

        if (savedItems.isNullOrEmpty()) {
            return
        }

        val cartItemsJson = JSONArray(savedItems)
        for (index in 0 until cartItemsJson.length()) {
            val itemJson = cartItemsJson.getJSONObject(index)
            val typesJson = itemJson.optJSONArray("types") ?: JSONArray()
            val types = mutableListOf<String>()
            for (typeIndex in 0 until typesJson.length()) {
                types.add(typesJson.optString(typeIndex))
            }

            val coffee = CoffeeModel(
                id = itemJson.getInt("coffeeId"),
                name = itemJson.optString("name"),
                description = itemJson.optString("description"),
                price = itemJson.optDouble("price"),
                types = types,
                imageResId = itemJson.optInt("imageResId"),
                imageDrawable = itemJson.optString("imageDrawable"),
                imageUrl = itemJson.optString("imageUrl")
            )
            val quantity = itemJson.getInt("quantity")
            items.add(CartItem(coffee, quantity))
        }
    }
}
