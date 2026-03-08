package com.example.coffeeshopapp.activities

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.CartAdapter
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.viewmodel.CartViewModel

class CartActivity : AppCompatActivity() {
    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        val rvCart = findViewById<RecyclerView>(R.id.rvCartItems)
        rvCart.layoutManager = LinearLayoutManager(this)

        val items = cartViewModel.getCartItems()
        rvCart.adapter = CartAdapter(items)

        val tvTotal: TextView = findViewById(R.id.tvTotalPrice)
        val total = cartViewModel.getTotalPrice()
        tvTotal.text = "Total: $${String.format("%.2f", total)}"

        val btnCheckout: Button = findViewById(R.id.btnCheckout)
        btnCheckout.setOnClickListener {
            if (items.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Order Successful!")
            builder.setMessage("Your coffee is being prepared.\nTotal: $${String.format("%.2f", total)}")
            builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
                cartViewModel.clearCart()
                cartViewModel.saveCart(this)
                finish()
            }
            builder.show()
        }
    }

    override fun onPause() {
        super.onPause()
        cartViewModel.saveCart(this)
    }
}
