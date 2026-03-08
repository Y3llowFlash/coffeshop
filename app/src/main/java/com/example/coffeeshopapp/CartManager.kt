package com.example.coffeeshopapp

import android.content.Context
import com.example.coffeeshopapp.model.CartItem
import com.example.coffeeshopapp.model.CoffeeModel
import com.example.coffeeshopapp.model.MockData
import org.json.JSONArray
import org.json.JSONObject

object CartManager {
    private const val PREFS_NAME = "coffee_shop_cart"
    private const val KEY_CART_SIZE = "cart_size"
    private const val KEY_TOTAL_PRICE = "total_price"
    private const val KEY_CART_ITEMS = "cart_items"

    private val items = mutableListOf<CartItem>()

    fun addItem(coffee: CoffeeModel) {
        val existingItem = items.find { it.coffee.id == coffee.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            items.add(CartItem(coffee, 1))
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

    fun saveCart(context: Context) {
        val cartItemsJson = JSONArray()
        for (item in items) {
            val itemJson = JSONObject()
                .put("coffeeId", item.coffee.id)
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
            val coffeeId = itemJson.getInt("coffeeId")
            val quantity = itemJson.getInt("quantity")
            val coffee = MockData.menuList.find { it.id == coffeeId } ?: continue
            items.add(CartItem(coffee, quantity))
        }
    }
}
