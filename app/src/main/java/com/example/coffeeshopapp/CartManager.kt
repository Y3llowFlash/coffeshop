package com.example.coffeeshopapp

object CartManager {
    // This list holds all the items the user has added
    private val items = mutableListOf<CartItem>()

    // 1. Add a coffee to the cart
    fun addItem(coffee: CoffeeModel) {
        // Logic: Check if this coffee is already in the cart
        val existingItem = items.find { it.coffee.id == coffee.id }

        if (existingItem != null) {
            // If it exists, just increase the quantity
            existingItem.quantity++
        } else {
            // If it's new, add it to the list with quantity 1
            items.add(CartItem(coffee, 1))
        }
    }

    // 2. Get all items (so we can display them later)
    fun getCartItems(): List<CartItem> {
        return items
    }

    // 3. Calculate the total cost (Price * Quantity)
    fun getTotalPrice(): Double {
        var total = 0.0
        for (item in items) {
            total += (item.coffee.price * item.quantity)
        }
        return total
    }

    // 4. Clear cart (for after purchase)
    fun clearCart() {
        items.clear()
    }
}