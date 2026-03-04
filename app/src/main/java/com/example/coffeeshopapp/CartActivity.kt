package com.example.coffeeshopapp

import android.content.DialogInterface // Fixes "Cannot infer type"
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast // Fixes "Unresolved reference: Toast"
import androidx.appcompat.app.AlertDialog // Fixes "Unresolved reference: AlertDialog"
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // 1. Setup List
        val rvCart = findViewById<RecyclerView>(R.id.rvCartItems)
        rvCart.layoutManager = LinearLayoutManager(this)

        // 2. Get Data from Singleton
        val items = CartManager.getCartItems()

        // 3. Attach Adapter
        rvCart.adapter = CartAdapter(items)

        // 4. Update Total Price
        val tvTotal: TextView = findViewById(R.id.tvTotalPrice)
        val total = CartManager.getTotalPrice()
        tvTotal.text = "Total: $${String.format("%.2f", total)}"

        // 5. Handle Checkout
        val btnCheckout: Button = findViewById(R.id.btnCheckout)

        btnCheckout.setOnClickListener {
            // Check if cart is empty first
            if (items.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Create the Success Dialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Order Successful!")
            builder.setMessage("Your coffee is being prepared.\nTotal: $${String.format("%.2f", total)}")

            // 2. What happens when they click "OK"?
            // We explicitly say 'dialog' is a DialogInterface and 'which' is an Int to fix the red error
            builder.setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                // Clear the memory
                CartManager.clearCart()

                // Close this screen and go back to Menu
                finish()
            }

            // 3. Show it
            builder.show()
        }
    }
}