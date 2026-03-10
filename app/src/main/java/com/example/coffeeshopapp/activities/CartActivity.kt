package com.example.coffeeshopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.CartAdapter
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.formatMMK
import com.example.coffeeshopapp.viewmodel.CartViewModel

class CartActivity : AppCompatActivity() {
    private lateinit var cartViewModel: CartViewModel
    private lateinit var tvTotal: TextView
    private lateinit var tvCartEmpty: TextView
    private lateinit var rvCart: RecyclerView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        rvCart = findViewById(R.id.rvCartItems)
        rvCart.layoutManager = LinearLayoutManager(this)

        val items = cartViewModel.getCartItems()
        cartAdapter = CartAdapter(
            items,
            onIncreaseQuantity = { item, position ->
                cartViewModel.increaseItemQuantity(item.coffee.id)
                cartAdapter.notifyItemChanged(position)
                refreshCartState()
            },
            onDecreaseQuantity = { item, position ->
                val shouldRemoveItem = item.quantity == 1
                cartViewModel.decreaseItemQuantity(item.coffee.id)
                if (shouldRemoveItem) {
                    cartAdapter.notifyItemRemoved(position)
                } else {
                    cartAdapter.notifyItemChanged(position)
                }
                refreshCartState()
            }
        )
        rvCart.adapter = cartAdapter

        tvTotal = findViewById(R.id.tvTotalPrice)
        tvCartEmpty = findViewById(R.id.tvCartEmpty)
        refreshCartState()

        val btnCheckout: Button = findViewById(R.id.btnCheckout)
        btnCheckout.setOnClickListener {
            if (items.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun refreshCartState() {
        val total = cartViewModel.getTotalPrice()
        tvTotal.text = "Total: ${formatMMK(total)}"
        if (cartViewModel.getCartItems().isEmpty()) {
            tvCartEmpty.visibility = View.VISIBLE
            rvCart.visibility = View.GONE
        } else {
            tvCartEmpty.visibility = View.GONE
            rvCart.visibility = View.VISIBLE
        }
        cartViewModel.saveCart(this)
    }

    override fun onPause() {
        super.onPause()
        cartViewModel.saveCart(this)
    }
}
